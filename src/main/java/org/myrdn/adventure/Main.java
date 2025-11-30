package org.myrdn.adventure;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import org.myrdn.adventure.config.GameConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Adventure game.
 * Initializes LibGDX and starts the game.
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        System.setProperty("file.encoding", "UTF-8");

        logger.info("Starting Adventure game...");

        GameConfig gameConfig = new GameConfig();

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("\uD83D\uDC31\u200D\uD83D\uDC64 Textadventure");
        config.setWindowedMode(gameConfig.getWindowWidth(), gameConfig.getWindowHeight());
        config.setResizable(gameConfig.isWindowResizable());
        config.useVsync(gameConfig.isVsyncEnabled());
        config.setForegroundFPS(gameConfig.getTargetFps());

        logger.info("Window size: {}x{}", gameConfig.getWindowWidth(), gameConfig.getWindowHeight());

        new Lwjgl3Application(new AdventureGame(), config);

    }

}
