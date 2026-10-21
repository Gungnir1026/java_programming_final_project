package com.trashsorter;

/** The four categories used by the original game artwork. */
enum TrashType {
    RECYCLABLE(1, "blue", "可回收物"),
    FOOD(2, "green", "廚餘垃圾"),
    GENERAL(3, "grey", "其他垃圾"),
    HAZARDOUS(4, "red", "有害垃圾");

    final int key;
    final String color;
    final String label;

    TrashType(int key, String color, String label) {
        this.key = key;
        this.color = color;
        this.label = label;
    }

    static TrashType fromKey(int key) {
        if (key < 1 || key > values().length) {
            throw new IllegalArgumentException("Trashcan index must be between 1 and 4.");
        }
        return values()[key - 1];
    }
}
