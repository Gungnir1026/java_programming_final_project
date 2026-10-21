package com.trashsorter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Random;

final class Trash extends GameObject {
    private final double speed;
    private final double rotationSpeed;
    private final int variant;
    private double angle;

    Trash(Random random, double speed) {
        super(TrashType.values()[random.nextInt(4)], 400 + random.nextInt(411), 0, 70, 70);
        this.speed = speed;
        variant = random.nextInt(3) + 1;
        angle = random.nextInt(360);
        rotationSpeed = (random.nextDouble() * 5 + 1) * 60 * (random.nextBoolean() ? 1 : -1);
    }

    void update(double seconds) {
        y += speed * seconds;
        angle = (angle + rotationSpeed * seconds) % 360;
    }

    void render(GraphicsContext gc) {
        gc.save();
        gc.translate(x + width / 2, y + height / 2);
        gc.rotate(angle);
        gc.drawImage(image(), -width / 2, -height / 2, width, height);
        gc.restore();
    }

    @Override
    Image image() {
        return Assets.image("trash/" + type.color + "/" + variant + ".jpg");
    }
}
