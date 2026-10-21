package com.trashsorter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class UpgradeTest {
    @TempDir
    Path directory;

    private Path savePath() {
        return directory.resolve("player/save.properties");
    }

    @Test
    void newPlayerStartsAtZeroWithoutCreatingAFileOnRead() {
        Upgrade progress = new Upgrade(savePath());
        assertEquals(0, progress.getCoins());
        assertEquals(0, progress.getLevel(1));
        assertEquals(500, progress.getUpgradeCost(1));
        assertEquals(100, progress.getReward(1));
        assertNull(progress.getPersistenceWarning());
        assertFalse(Files.exists(savePath()));
    }

    @Test
    void rewardsAndPurchasesSurviveRestart() {
        Upgrade progress = new Upgrade(savePath());
        progress.awardCoins(1500);
        assertTrue(progress.buyUpgrade(2));
        assertEquals(1000, progress.getCoins());
        assertEquals(1, progress.getLevel(2));
        assertEquals(1000, progress.getUpgradeCost(2));
        assertEquals(110, progress.getReward(2));

        Upgrade reloaded = new Upgrade(savePath());
        assertEquals(1000, reloaded.getCoins());
        assertEquals(1, reloaded.getLevel(2));
        assertEquals(0, reloaded.getLevel(1));
        assertNull(reloaded.getPersistenceWarning());
    }

    @Test
    void unaffordablePurchaseDoesNotChangeProgress() {
        Upgrade progress = new Upgrade(savePath());
        progress.awardCoins(499);
        assertFalse(progress.buyUpgrade(1));
        assertEquals(499, new Upgrade(savePath()).getCoins());
        assertEquals(0, new Upgrade(savePath()).getLevel(1));
    }

    @Test
    void readsOriginalFinalProjectSaveFormat() throws IOException {
        Path original = directory.resolve("save.txt");
        Files.writeString(original, "trashcan2=4\ntrashcan3=1\ntrashcan1=1\ncoins=3000\ntrashcan4=1\n");
        Upgrade progress = new Upgrade(original);
        assertEquals(3000, progress.getCoins());
        assertEquals(4, progress.getLevel(2));
        assertEquals(8000, progress.getUpgradeCost(2));
        assertEquals(140, progress.getReward(2));
    }

    @Test
    void malformedAndOutOfRangeFieldsDoNotCrashTheGame() throws IOException {
        Path save = directory.resolve("bad.properties");
        Files.writeString(save, "coins=hello\ntrashcan1=-3\ntrashcan2=9000\ntrashcan3=abc\n");
        Upgrade progress = new Upgrade(save);
        assertEquals(0, progress.getCoins());
        assertEquals(0, progress.getLevel(1));
        assertEquals(Upgrade.MAX_LEVEL, progress.getLevel(2));
        assertEquals(0, progress.getLevel(3));
        assertNotNull(progress.getPersistenceWarning());
    }

    @Test
    void coinsAreCappedBeforeIntegerOverflow() {
        Upgrade progress = new Upgrade(savePath());
        progress.awardCoins(Integer.MAX_VALUE);
        progress.awardCoins(100);
        assertEquals(Upgrade.MAX_COINS, progress.getCoins());
        assertEquals(Upgrade.MAX_COINS, new Upgrade(savePath()).getCoins());
    }

    @Test
    void maxLevelCannotBePurchasedAgain() throws IOException {
        Path save = directory.resolve("max.properties");
        Files.writeString(save, "coins=9999999\ntrashcan1=15\n");
        Upgrade progress = new Upgrade(save);
        assertFalse(progress.buyUpgrade(1));
        assertEquals(15, progress.getLevel(1));
        assertEquals(9_999_999, progress.getCoins());
    }

    @Test
    void invalidRequestsAreRejectedWithoutChangingBalance() {
        Upgrade progress = new Upgrade(savePath());
        assertThrows(IllegalArgumentException.class, () -> progress.buyUpgrade(0));
        assertThrows(IllegalArgumentException.class, () -> progress.getLevel(5));
        assertThrows(IllegalArgumentException.class, () -> progress.awardCoins(-1));
        assertEquals(0, progress.getCoins());
    }

    @Test
    void failedSaveIsVisibleAndDoesNotDestroyExistingData() throws IOException {
        Path blockedParent = directory.resolve("not-a-directory");
        Files.writeString(blockedParent, "keep this data");
        Upgrade progress = new Upgrade(blockedParent.resolve("save.properties"));
        progress.awardCoins(100);
        assertEquals(100, progress.getCoins());
        assertNotNull(progress.getPersistenceWarning());
        assertEquals("keep this data", Files.readString(blockedParent));
    }
}
