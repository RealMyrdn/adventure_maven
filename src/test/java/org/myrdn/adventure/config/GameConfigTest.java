package org.myrdn.adventure.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GameConfig class.
 */
class GameConfigTest {

    private GameConfig config;

    @BeforeEach
    void setUp() {
        config = new GameConfig();
    }

    @Test
    void testGetDungeonWidth() {
        int width = config.getDungeonWidth();
        assertTrue(width > 0, "Dungeon width should be positive");
    }

    @Test
    void testGetDungeonHeight() {
        int height = config.getDungeonHeight();
        assertTrue(height > 0, "Dungeon height should be positive");
    }

    @Test
    void testGetPlayerName() {
        String name = config.getPlayerName();
        assertNotNull(name, "Player name should not be null");
        assertFalse(name.isEmpty(), "Player name should not be empty");
    }

    @Test
    void testGetPlayerStartX() {
        int startX = config.getPlayerStartX();
        assertTrue(startX >= 0, "Player start X should be non-negative");
    }

    @Test
    void testGetPlayerStartY() {
        int startY = config.getPlayerStartY();
        assertTrue(startY >= 0, "Player start Y should be non-negative");
    }

    @Test
    void testIsAutosaveEnabled() {
        boolean autosave = config.isAutosaveEnabled();
        // Should be either true or false, not null
        assertNotNull(autosave);
    }

    @Test
    void testGetAutosaveIntervalMinutes() {
        int interval = config.getAutosaveIntervalMinutes();
        assertTrue(interval > 0, "Autosave interval should be positive");
    }

    @Test
    void testGetMaxCommandLength() {
        int maxLength = config.getMaxCommandLength();
        assertTrue(maxLength > 0, "Max command length should be positive");
    }
}
