package com.trashsorter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

final class Hud {
    private Hud() {
    }

    static void coins(GraphicsContext gc, int coins) {
        gc.setFill(Color.BLACK);
        gc.fillRect(900, 8, 370, 82);
        gc.drawImage(Assets.image("etc/coin.jpg"), 905, 13, 70, 70);
        String digits = String.format(java.util.Locale.ROOT, "%07d", coins);
        for (int i = 0; i < digits.length(); i++) {
            gc.drawImage(Assets.image("etc/" + digits.charAt(i) + ".jpg"),
                    978 + i * 40, 14, 65, 65);
        }
    }

    static void text(GraphicsContext gc, String text, double x, double y, int size, Color color) {
        gc.setFill(color);
        gc.setFont(Font.font("System", FontWeight.BOLD, size));
        gc.fillText(text, x, y);
    }
}
