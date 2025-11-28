package org.myrdn.adventure.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration manager for the game.
 * Loads and provides access to game configuration properties.
 */
public class GameConfig {

    private static final Logger logger = LoggerFactory.getLogger(GameConfig.class);
    private static final String CONFIG_FILE = "game.properties";

    private final Properties properties;

    public GameConfig() {
        this.properties = new Properties();
        loadConfig();
    }

    private void loadConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                logger.warn("Unable to find {}, using default values", CONFIG_FILE);
                setDefaults();
                return;
            }
            properties.load(input);
            logger.info("Configuration loaded successfully from {}", CONFIG_FILE);
        } catch (IOException e) {
            logger.error("Error loading configuration", e);
            setDefaults();
        }
    }

    private void setDefaults() {
        properties.setProperty("dungeon.width", "13");
        properties.setProperty("dungeon.height", "7");
        properties.setProperty("player.name", "Myrdn");
        properties.setProperty("player.start.x", "3");
        properties.setProperty("player.start.y", "3");
        properties.setProperty("autosave.enabled", "true");
        properties.setProperty("autosave.interval.minutes", "5");
        properties.setProperty("game.max.command.length", "80");
    }

    public int getDungeonWidth() {
        return Integer.parseInt(properties.getProperty("dungeon.width", "13"));
    }

    public int getDungeonHeight() {
        return Integer.parseInt(properties.getProperty("dungeon.height", "7"));
    }

    public String getPlayerName() {
        return properties.getProperty("player.name", "Myrdn");
    }

    public int getPlayerStartX() {
        return Integer.parseInt(properties.getProperty("player.start.x", "3"));
    }

    public int getPlayerStartY() {
        return Integer.parseInt(properties.getProperty("player.start.y", "3"));
    }

    public boolean isAutosaveEnabled() {
        return Boolean.parseBoolean(properties.getProperty("autosave.enabled", "true"));
    }

    public int getAutosaveIntervalMinutes() {
        return Integer.parseInt(properties.getProperty("autosave.interval.minutes", "5"));
    }

    public int getMaxCommandLength() {
        return Integer.parseInt(properties.getProperty("game.max.command.length", "80"));
    }
}
