package com.trashsorter;

import javafx.scene.image.Image;

/** Common position and bounds for falling trash and the player bin. */
abstract class GameObject {
    protected TrashType type;
    protected double x;
    protected double y;
    protected final double width;
    protected final double height;

    GameObject(TrashType type, double x, double y, double width, double height) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    abstract Image image();

    final boolean intersects(GameObject other) {
        return x < other.x + other.width && x + width > other.x
                && y < other.y + other.height && y + height > other.y;
    }
}
