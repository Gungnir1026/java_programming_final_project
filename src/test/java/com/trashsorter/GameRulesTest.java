package com.trashsorter;

import org.junit.jupiter.api.Test;
import javafx.scene.image.Image;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameRulesTest {
    @Test
    void binCannotLeaveTheBeach() {
        Trashcan bin = new Trashcan();
        bin.move(-1, 100);
        assertEquals(400, bin.x);
        bin.move(1, 100);
        assertEquals(800, bin.x);
    }

    @Test
    void movementAndFallingUseElapsedTime() {
        Trashcan oneFrame = new Trashcan();
        Trashcan tenFrames = new Trashcan();
        oneFrame.move(1, 0.1);
        for (int i = 0; i < 10; i++) {
            tenFrames.move(1, 0.01);
        }
        assertEquals(oneFrame.x, tenFrames.x, 0.0001);
        Trash item = new Trash(new Random(1), 180);
        item.update(0.5);
        assertEquals(90, item.y);
    }

    @Test
    void collisionRequiresOverlapOnBothAxes() {
        GameObject bin = box(400, 550, 80, 100);
        assertTrue(bin.intersects(box(410, 500, 70, 70)));
        assertFalse(bin.intersects(box(480, 550, 70, 70)));
        assertFalse(bin.intersects(box(410, 651, 70, 70)));
        assertFalse(bin.intersects(box(410, 480, 70, 70)));
    }

    private GameObject box(double x, double y, double width, double height) {
        return new GameObject(TrashType.RECYCLABLE, x, y, width, height) {
            @Override
            Image image() {
                throw new UnsupportedOperationException("Collision tests do not load images.");
            }
        };
    }
}
