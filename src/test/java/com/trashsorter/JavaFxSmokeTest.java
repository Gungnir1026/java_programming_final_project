package com.trashsorter;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

import static org.junit.jupiter.api.Assertions.*;

/** Requires a desktop session, or xvfb-run on Linux. Never touches the real player save. */
@EnabledIfSystemProperty(named = "ui.tests", matches = "true")
class JavaFxSmokeTest {
    @TempDir
    Path temporary;

    private Stage stage;
    private Main application;
    private final Path screenshots = Path.of(System.getProperty("trashsorter.screenshots", "target/ui-screenshots"));

    @Test
    void loadsAllAssetsAndExercisesMenuGameAndShop() throws Exception {
        Path save = temporary.resolve("save.properties");
        Files.writeString(save, "coins=1500\n");
        String previousSave = System.getProperty("trashsorter.save");
        System.setProperty("trashsorter.save", save.toString());
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(started::countDown);
        assertTrue(started.await(20, TimeUnit.SECONDS));
        Platform.setImplicitExit(false);
        try {
            onFx(() -> {
                for (String scene : new String[]{"Menu", "upgrade", "beach"}) {
                    assertFalse(Assets.image("scene/" + scene + ".jpg").isError());
                }
                for (TrashType type : TrashType.values()) {
                    for (String suffix : new String[]{"", "_c"}) {
                        assertFalse(Assets.image("trashcan/" + type.color + suffix + ".jpg").isError());
                    }
                    for (int i = 1; i <= 3; i++) {
                        assertFalse(Assets.image("trash/" + type.color + "/" + i + ".jpg").isError());
                    }
                    assertFalse(Assets.image("wrong/" + type.key + ".png").isError());
                }
                for (int i = 0; i <= 9; i++) {
                    assertFalse(Assets.image("etc/" + i + ".jpg").isError());
                }
                for (String path : new String[]{"etc/coin.jpg", "etc/heart.jpg", "wrong/tip.png", "wrong/no_coin.png"}) {
                    assertFalse(Assets.image(path).isError());
                }
                assertThrows(IllegalArgumentException.class, () -> Assets.image("missing.png"));
                application = new Main();
                stage = new Stage();
                application.start(stage);
                return null;
            });
            long menu = capture("menu");
            tap(KeyCode.DIGIT1);
            long tip = capture("instructions");
            assertNotEquals(menu, tip);
            tap(KeyCode.ENTER);
            Thread.sleep(1200);
            long game = capture("gameplay");
            assertNotEquals(tip, game);
            tap(KeyCode.NUMPAD4);
            tap(KeyCode.LEFT);
            tap(KeyCode.ESCAPE);
            tap(KeyCode.DIGIT2);
            long shop = capture("upgrade");
            assertNotEquals(menu, shop);
            assertEquals(1500, new Upgrade(save).getCoins(), "Entering the shop must not buy an upgrade.");

            // Repeated KEY_PRESSED events while a key is held must buy only once.
            onFx(() -> {
                key(KeyEvent.KEY_PRESSED, KeyCode.DIGIT1);
                key(KeyEvent.KEY_PRESSED, KeyCode.DIGIT1);
                key(KeyEvent.KEY_RELEASED, KeyCode.DIGIT1);
                return null;
            });
            assertEquals(1000, new Upgrade(save).getCoins());
            assertEquals(1, new Upgrade(save).getLevel(1));
            tap(KeyCode.DIGIT1);
            assertEquals(0, new Upgrade(save).getCoins());
            assertEquals(2, new Upgrade(save).getLevel(1));
            tap(KeyCode.DIGIT3);
            capture("insufficient-coins");
            assertEquals(0, new Upgrade(save).getLevel(3));
            tap(KeyCode.ESCAPE);
            assertEquals(menu, capture("menu-return"));

            onFx(() -> {
                Game round = new Game(new Upgrade(temporary.resolve("game-over.properties")));
                for (int i = 0; i < 12_000 && !round.isOver(); i++) {
                    round.update(0.05, false, false);
                }
                assertTrue(round.isOver(), "Missed trash must eventually end a round.");
                for (int i = 0; i < 101; i++) {
                    round.update(0.05, false, false);
                }
                Canvas canvas = new Canvas(Main.WIDTH, Main.HEIGHT);
                round.render(canvas.getGraphicsContext2D());
                writeImage("game-over", canvas.snapshot(null, null));
                return null;
            });
        } finally {
            onFx(() -> {
                if (application != null) {
                    application.stop();
                }
                if (stage != null) {
                    stage.close();
                }
                return null;
            });
            Platform.exit();
            if (previousSave == null) {
                System.clearProperty("trashsorter.save");
            } else {
                System.setProperty("trashsorter.save", previousSave);
            }
        }
    }

    private void tap(KeyCode code) throws Exception {
        onFx(() -> {
            key(KeyEvent.KEY_PRESSED, code);
            key(KeyEvent.KEY_RELEASED, code);
            return null;
        });
    }

    private void key(javafx.event.EventType<KeyEvent> eventType, KeyCode code) {
        Event.fireEvent(stage.getScene(), new KeyEvent(eventType, "", "", code, false, false, false, false));
    }

    private long capture(String name) throws Exception {
        // Allow the AnimationTimer to draw after input, then snapshot on the FX thread.
        Thread.sleep(150);
        return onFx(() -> {
            Scene scene = stage.getScene();
            WritableImage image = scene.snapshot(null);
            assertEquals(Main.WIDTH, image.getWidth());
            assertEquals(Main.HEIGHT, image.getHeight());
            writeImage(name, image);
            CRC32 hash = new CRC32();
            for (int y = 0; y < Main.HEIGHT; y++) {
                for (int x = 0; x < Main.WIDTH; x++) {
                    int pixel = image.getPixelReader().getArgb(x, y);
                    hash.update(pixel >>> 24);
                    hash.update(pixel >>> 16);
                    hash.update(pixel >>> 8);
                    hash.update(pixel);
                }
            }
            return hash.getValue();
        });
    }

    private void writeImage(String name, WritableImage image) throws Exception {
        Files.createDirectories(screenshots);
        assertTrue(ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", screenshots.resolve(name + ".png").toFile()));
    }

    private <T> T onFx(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(20, TimeUnit.SECONDS);
    }
}
