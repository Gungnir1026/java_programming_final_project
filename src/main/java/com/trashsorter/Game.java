package com.trashsorter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/** One round. Player progress is shared with the shop through Upgrade. */
final class Game {
    private final Upgrade progress;
    private final Trashcan trashcan = new Trashcan();
    private final List<Trash> trash = new ArrayList<>();
    private final Random random = new Random();
    private int lives = 3;
    private int spawned;
    private double spawnTime;
    private double spawnInterval = 5;
    private double speed = 180;
    private double explanationTime;
    private TrashType wrongType;

    Game(Upgrade progress) {
        this.progress = progress;
    }

    boolean isOver() {
        return lives <= 0;
    }

    void select(int index) {
        trashcan.select(index);
    }

    void update(double seconds, boolean left, boolean right) {
        if (explanationTime > 0) {
            explanationTime = Math.max(0, explanationTime - seconds);
            return;
        }
        if (isOver()) {
            return;
        }
        trashcan.move((right ? 1 : 0) - (left ? 1 : 0), seconds);
        spawnTime -= seconds;
        if (spawnTime <= 0) {
            trash.add(new Trash(random, speed));
            spawnTime = spawnInterval;
            spawned++;
            if (spawned % 6 == 5) {
                speed += 60;
                spawnInterval += 1;
            } else if (spawned % 6 == 0) {
                spawnInterval = Math.max(0.65, spawnInterval - 1.5);
            }
        }

        Iterator<Trash> iterator = trash.iterator();
        while (iterator.hasNext()) {
            Trash item = iterator.next();
            item.update(seconds);
            if (item.intersects(trashcan)) {
                iterator.remove();
                if (item.type == trashcan.type) {
                    progress.awardCoins(progress.getReward(trashcan.type.key));
                } else {
                    lives--;
                    wrongType = item.type;
                    explanationTime = 5;
                    break;
                }
            } else if (item.y + item.height >= Main.HEIGHT) {
                lives--;
                iterator.remove();
            }
            if (isOver()) {
                break;
            }
        }
    }

    void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
        gc.drawImage(Assets.image("scene/beach.jpg"), 400, 0);
        gc.drawImage(trashcan.image(), trashcan.x, trashcan.y);

        for (TrashType type : TrashType.values()) {
            String selected = type == trashcan.type ? "_c" : "";
            gc.drawImage(Assets.image("trashcan/" + type.color + selected + ".jpg"),
                    30 + type.ordinal() * 90, 40, 70, 85);
            Hud.text(gc, Integer.toString(type.key), 60 + type.ordinal() * 90, 160, 30, Color.WHITE);
        }
        Hud.text(gc, "← → 移動  |  1–4 切換分類", 24, 215, 20, Color.WHITE);
        Hud.text(gc, trashcan.type.label, 24, 255, 24, Color.LIGHTGREEN);
        Hud.text(gc, "ESC 回主選單", 24, 690, 20, Color.WHITE);
        Hud.coins(gc, progress.getCoins());
        for (int i = 0; i < lives; i++) {
            gc.drawImage(Assets.image("etc/heart.jpg"), 1200 - i * 60, 105, 60, 60);
        }
        for (Trash item : trash) {
            item.render(gc);
        }

        if (explanationTime > 0) {
            gc.drawImage(Assets.image("wrong/" + wrongType.key + ".png"), 110, 70, 1026, 576);
        } else if (isOver()) {
            gc.setFill(Color.rgb(0, 0, 0, 0.75));
            gc.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
            Hud.text(gc, "Game Over", 470, 340, 60, Color.TOMATO);
            Hud.text(gc, "R 再玩一次  |  ESC 回主選單", 420, 405, 28, Color.WHITE);
        }
    }
}
