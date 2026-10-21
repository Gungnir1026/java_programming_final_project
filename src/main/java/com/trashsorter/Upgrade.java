package com.trashsorter;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/** Shared player progress. Rendering never reads or writes the save file. */
public final class Upgrade {
    public static final int MAX_COINS = 9_999_999;
    public static final int MAX_LEVEL = 15;

    private final Path savePath;
    private int coins;
    private final int[] levels = new int[4];
    private String persistenceWarning;

    public Upgrade() {
        this(defaultSavePath());
    }

    public Upgrade(Path savePath) {
        this.savePath = savePath.toAbsolutePath().normalize();
        load();
    }

    private static Path defaultSavePath() {
        String override = System.getProperty("trashsorter.save");
        return override == null || override.isBlank()
                ? Path.of(System.getProperty("user.home"), ".trash-sorting-game", "save.properties")
                : Path.of(override);
    }

    private void load() {
        if (!Files.exists(savePath)) {
            return;
        }
        Properties data = new Properties();
        try (Reader reader = Files.newBufferedReader(savePath, StandardCharsets.UTF_8)) {
            data.load(reader);
            coins = readNumber(data, "coins", MAX_COINS);
            for (int i = 0; i < levels.length; i++) {
                levels[i] = readNumber(data, "trashcan" + (i + 1), MAX_LEVEL);
            }
        } catch (IOException | IllegalArgumentException e) {
            warn("無法完整讀取存檔，請檢查：" + savePath);
        }
    }

    private int readNumber(Properties data, String key, int maximum) {
        String text = data.getProperty(key, "0");
        try {
            long value = Long.parseLong(text.trim());
            if (value < 0 || value > maximum) {
                warn("存檔數值超出範圍，已調整：" + key);
            }
            return (int) Math.clamp(value, 0, maximum);
        } catch (NumberFormatException e) {
            warn("存檔數值無效，已使用 0：" + key);
            return 0;
        }
    }

    public int getCoins() {
        return coins;
    }

    public int getLevel(int index) {
        return levels[TrashType.fromKey(index).ordinal()];
    }

    public long getUpgradeCost(int index) {
        return 500L << getLevel(index);
    }

    public int getReward(int index) {
        return 100 + 10 * getLevel(index);
    }

    public void awardCoins(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Coin reward must not be negative.");
        }
        coins = (int) Math.min(MAX_COINS, (long) coins + amount);
        save();
    }

    /** Purchase and persist both the balance and level in one operation. */
    public boolean buyUpgrade(int index) {
        int position = TrashType.fromKey(index).ordinal();
        long cost = getUpgradeCost(index);
        if (levels[position] >= MAX_LEVEL || coins < cost) {
            return false;
        }
        coins -= (int) cost;
        levels[position]++;
        save();
        return true;
    }

    public Path getSavePath() {
        return savePath;
    }

    public String getPersistenceWarning() {
        return persistenceWarning;
    }

    private void save() {
        Path temporary = null;
        try {
            Files.createDirectories(savePath.getParent());
            temporary = Files.createTempFile(savePath.getParent(), "progress-", ".tmp");
            Properties data = new Properties();
            data.setProperty("coins", Integer.toString(coins));
            for (int i = 0; i < levels.length; i++) {
                data.setProperty("trashcan" + (i + 1), Integer.toString(levels[i]));
            }
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                data.store(writer, "Trash Sorting Game progress");
            }
            try {
                Files.move(temporary, savePath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, savePath, StandardCopyOption.REPLACE_EXISTING);
            }
            persistenceWarning = null;
        } catch (IOException | SecurityException e) {
            warn("進度尚未寫入磁碟，請檢查存檔路徑：" + savePath);
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException ignored) {
                    // A leftover temporary file must not interrupt the game.
                }
            }
        }
    }

    private void warn(String message) {
        persistenceWarning = message;
        System.err.println(message);
    }
}
