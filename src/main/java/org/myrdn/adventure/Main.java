package org.myrdn.adventure;

import java.io.IOException;

import org.myrdn.adventure.gamecontroller.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the Adventure game.
 * Initializes the game and starts the main game loop.
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        System.setProperty("file.encoding","UTF-8");

        logger.info("Starting Adventure game...");

        try {

            Game game = new Game();
            game.init();

        } catch(IOException e) {

            logger.error("Virtuelle Konsole konnte nicht initialisiert werden", e);
            System.out.println("Fehler beim Starten des Spiels. Siehe logs/adventure.log für Details.");
            System.exit(1);

        }

    }

}