package com.trashsorter;

import javafx.scene.image.Image;

final class Trashcan extends GameObject {
    private static final double MOVE_SPEED = 600;

    Trashcan() {
        super(TrashType.RECYCLABLE, 580, 550, 80, 100);
    }

    void move(double direction, double seconds) {
        x = Math.clamp(x + direction * MOVE_SPEED * seconds, 400, 880 - width);
    }

    void select(int index) {
        type = TrashType.fromKey(index);
    }

    @Override
    Image image() {
        return Assets.image("trashcan/" + type.color + ".jpg");
    }
}
