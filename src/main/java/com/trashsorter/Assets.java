package com.trashsorter;

import javafx.scene.image.Image;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/** Load bundled resources once, independent of the working directory. */
final class Assets {
    private static final Map<String, Image> CACHE = new HashMap<>();

    private Assets() {
    }

    static Image image(String path) {
        return CACHE.computeIfAbsent(path, key -> {
            URL resource = Assets.class.getResource("/img/" + key);
            if (resource == null) {
                throw new IllegalArgumentException("Missing image resource: /img/" + key);
            }
            Image image = new Image(resource.toExternalForm());
            if (image.isError()) {
                throw new IllegalStateException("Cannot load image: " + key, image.getException());
            }
            return image;
        });
    }
}
