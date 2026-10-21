package com.trashsorter;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public final class Main extends Application {
    static final int WIDTH = 1280;
    static final int HEIGHT = 720;

    private enum State { MENU, TIP, GAME, UPGRADE }

    private State state = State.MENU;
    private final Set<KeyCode> pressed = new HashSet<>();
    private Upgrade progress;
    private Game game;
    private AnimationTimer loop;
    private GraphicsContext gc;
    private double noticeTime;
    private String notice;

    @Override
    public void start(Stage stage) {
        progress = new Upgrade();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        Scene scene = new Scene(new StackPane(canvas), WIDTH, HEIGHT);
        scene.setOnKeyPressed(event -> {
            if (pressed.add(event.getCode())) {
                handleKey(event.getCode());
            }
        });
        scene.setOnKeyReleased(event -> pressed.remove(event.getCode()));
        stage.focusedProperty().addListener((observable, previous, focused) -> {
            if (!focused) {
                pressed.clear();
            }
        });

        loop = new AnimationTimer() {
            private long previous;

            @Override
            public void handle(long now) {
                double seconds = previous == 0 ? 0 : Math.min((now - previous) / 1_000_000_000.0, 0.05);
                previous = now;
                noticeTime = Math.max(0, noticeTime - seconds);
                if (state == State.GAME) {
                    game.update(seconds, pressed.contains(KeyCode.LEFT), pressed.contains(KeyCode.RIGHT));
                }
                render();
            }
        };
        stage.setTitle("分類小達人 | Trash Sorting Game");
        stage.setScene(scene);
        stage.setResizable(false);
        render();
        stage.show();
        loop.start();
    }

    private void handleKey(KeyCode key) {
        int number = switch (key) {
            case DIGIT1, NUMPAD1 -> 1;
            case DIGIT2, NUMPAD2 -> 2;
            case DIGIT3, NUMPAD3 -> 3;
            case DIGIT4, NUMPAD4 -> 4;
            default -> 0;
        };
        switch (state) {
            case MENU -> {
                if (number == 1) {
                    state = State.TIP;
                } else if (number == 2) {
                    state = State.UPGRADE;
                    noticeTime = 0;
                }
            }
            case TIP -> {
                if (key == KeyCode.ESCAPE || key == KeyCode.ENTER) {
                    startRound();
                }
            }
            case GAME -> {
                if (key == KeyCode.ESCAPE) {
                    state = State.MENU;
                } else if (key == KeyCode.R && game.isOver()) {
                    startRound();
                } else if (number > 0) {
                    game.select(number);
                }
            }
            case UPGRADE -> {
                if (key == KeyCode.ESCAPE) {
                    state = State.MENU;
                } else if (number > 0 && !progress.buyUpgrade(number)) {
                    notice = progress.getLevel(number) >= Upgrade.MAX_LEVEL
                            ? "此分類已達最高等級" : "金幣不足，先到遊戲中收集金幣！";
                    noticeTime = 2;
                }
            }
        }
    }

    private void startRound() {
        game = new Game(progress);
        state = State.GAME;
    }

    private void render() {
        switch (state) {
            case MENU -> {
                gc.drawImage(Assets.image("scene/Menu.jpg"), 0, 0, WIDTH, HEIGHT);
                Hud.text(gc, "按 1 開始遊戲  ·  按 2 進入升級商店", 400, 675, 22, Color.DARKSLATEGRAY);
            }
            case TIP -> {
                gc.drawImage(Assets.image("wrong/tip.png"), 0, 0, WIDTH, HEIGHT);
                Hud.text(gc, "← → 移動  ·  1–4 切換垃圾桶  ·  ENTER / ESC 開始", 300, 672, 22, Color.WHITE);
            }
            case GAME -> game.render(gc);
            case UPGRADE -> drawUpgrade();
        }
        if (progress.getPersistenceWarning() != null) {
            gc.setFill(Color.DARKRED);
            gc.fillRect(0, HEIGHT - 32, WIDTH, 32);
            Hud.text(gc, "存檔有問題，請查看終端機訊息；目前仍可繼續遊玩。", 20, HEIGHT - 10, 18, Color.WHITE);
        }
    }

    private void drawUpgrade() {
        gc.drawImage(Assets.image("scene/upgrade.jpg"), 0, 0, WIDTH, HEIGHT);
        Hud.coins(gc, progress.getCoins());
        int[] centers = {250, 510, 770, 1030};
        for (TrashType type : TrashType.values()) {
            int center = centers[type.ordinal()];
            int level = progress.getLevel(type.key);
            Hud.text(gc, type.key + ". " + type.label, center - 80, 260, 22, Color.DARKSLATEGRAY);
            String cost = level >= Upgrade.MAX_LEVEL ? "MAX" : progress.getUpgradeCost(type.key) + " coins";
            Hud.text(gc, cost, center - 75, 302, 22, Color.DARKSLATEGRAY);
            // Cover the baked-in level labels before drawing dynamic values.
            gc.setFill(Color.rgb(239, 241, 207));
            gc.fillRect(center - 95, 540, 190, 48);
            Hud.text(gc, "Level " + level, center - 65, 573, 30, Color.DARKSLATEGRAY);
            Hud.text(gc, "+" + progress.getReward(type.key) + " coins / 次", center - 90, 615, 20, Color.DARKSLATEGRAY);
        }
        Hud.text(gc, "1–4 升級對應分類  ·  ESC 回主選單", 420, 688, 22, Color.DARKSLATEGRAY);
        if (noticeTime > 0) {
            gc.setFill(Color.rgb(0, 0, 0, 0.85));
            gc.fillRoundRect(320, 135, 640, 65, 18, 18);
            Hud.text(gc, notice, 355, 177, 26, Color.WHITE);
        }
    }

    @Override
    public void stop() {
        if (loop != null) {
            loop.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
