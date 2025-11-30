package org.myrdn.adventure.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

import org.myrdn.adventure.AdventureGame;
import org.myrdn.adventure.datahandler.DataHandler;
import org.myrdn.adventure.datahandler.SaveGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainMenuScreen extends BaseScreen {

    private static final Logger logger = LoggerFactory.getLogger(MainMenuScreen.class);

    private enum MenuState {
        MAIN,
        LOAD_GAME,
        NEW_GAME_NAME,
        SETTINGS,
        CREDITS
    }

    private static final String[] MAIN_MENU_ITEMS = {
            "Neues Spiel",
            "Spiel laden",
            "Einstellungen",
            "Credits",
            "Beenden"
    };

    private static final String[] SETTINGS_ITEMS = {
            "Autosave: ",
            "Zurück"
    };

    private MenuState currentState = MenuState.MAIN;
    private int selectedIndex = 0;
    private List<String> savedGames = new ArrayList<>();
    private StringBuilder playerNameInput = new StringBuilder();
    private String errorMessage = "";
    private boolean autosaveEnabled = true;

    public MainMenuScreen(AdventureGame game) {
        super(game);
        loadSavedGames();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                handleInput(keycode);
                return true;
            }

            @Override
            public boolean keyTyped(char character) {
                if (currentState == MenuState.NEW_GAME_NAME) {
                    handleNameInput(character);
                }
                return true;
            }
        });
    }

    private void handleInput(int keycode) {
        errorMessage = "";

        switch (currentState) {
            case MAIN -> handleMainMenuInput(keycode);
            case LOAD_GAME -> handleLoadGameInput(keycode);
            case NEW_GAME_NAME -> handleNewGameNameInput(keycode);
            case SETTINGS -> handleSettingsInput(keycode);
            case CREDITS -> handleCreditsInput(keycode);
        }
    }

    private void handleMainMenuInput(int keycode) {
        switch (keycode) {
            case Input.Keys.UP, Input.Keys.W -> {
                selectedIndex = (selectedIndex - 1 + MAIN_MENU_ITEMS.length) % MAIN_MENU_ITEMS.length;
            }
            case Input.Keys.DOWN, Input.Keys.S -> {
                selectedIndex = (selectedIndex + 1) % MAIN_MENU_ITEMS.length;
            }
            case Input.Keys.ENTER -> {
                switch (selectedIndex) {
                    case 0 -> { // Neues Spiel
                        currentState = MenuState.NEW_GAME_NAME;
                        playerNameInput.setLength(0);
                    }
                    case 1 -> { // Spiel laden
                        loadSavedGames();
                        if (savedGames.isEmpty()) {
                            errorMessage = "Keine Spielstände gefunden!";
                        } else {
                            currentState = MenuState.LOAD_GAME;
                            selectedIndex = 0;
                        }
                    }
                    case 2 -> { // Einstellungen
                        currentState = MenuState.SETTINGS;
                        selectedIndex = 0;
                    }
                    case 3 -> { // Credits
                        currentState = MenuState.CREDITS;
                    }
                    case 4 -> { // Beenden
                        Gdx.app.exit();
                    }
                }
            }
        }
    }

    private void handleLoadGameInput(int keycode) {
        int itemCount = savedGames.size() + 1; // +1 für "Zurück"

        switch (keycode) {
            case Input.Keys.UP, Input.Keys.W -> {
                selectedIndex = (selectedIndex - 1 + itemCount) % itemCount;
            }
            case Input.Keys.DOWN, Input.Keys.S -> {
                selectedIndex = (selectedIndex + 1) % itemCount;
            }
            case Input.Keys.ENTER -> {
                if (selectedIndex < savedGames.size()) {
                    loadGame(savedGames.get(selectedIndex));
                } else {
                    currentState = MenuState.MAIN;
                    selectedIndex = 1;
                }
            }
            case Input.Keys.ESCAPE -> {
                currentState = MenuState.MAIN;
                selectedIndex = 1;
            }
        }
    }

    private void handleNewGameNameInput(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            currentState = MenuState.MAIN;
            selectedIndex = 0;
        } else if (keycode == Input.Keys.ENTER && playerNameInput.length() > 0) {
            startNewGame(playerNameInput.toString());
        }
    }

    private void handleNameInput(char character) {
        if (character == '\b') {
            if (playerNameInput.length() > 0) {
                playerNameInput.deleteCharAt(playerNameInput.length() - 1);
            }
        } else if (!Character.isISOControl(character) && playerNameInput.length() < 20) {
            playerNameInput.append(character);
        }
    }

    private void handleSettingsInput(int keycode) {
        switch (keycode) {
            case Input.Keys.UP, Input.Keys.W -> {
                selectedIndex = (selectedIndex - 1 + SETTINGS_ITEMS.length) % SETTINGS_ITEMS.length;
            }
            case Input.Keys.DOWN, Input.Keys.S -> {
                selectedIndex = (selectedIndex + 1) % SETTINGS_ITEMS.length;
            }
            case Input.Keys.ENTER, Input.Keys.LEFT, Input.Keys.RIGHT -> {
                if (selectedIndex == 0) {
                    autosaveEnabled = !autosaveEnabled;
                } else if (selectedIndex == 1) {
                    currentState = MenuState.MAIN;
                    selectedIndex = 2;
                }
            }
            case Input.Keys.ESCAPE -> {
                currentState = MenuState.MAIN;
                selectedIndex = 2;
            }
        }
    }

    private void handleCreditsInput(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.ENTER) {
            currentState = MenuState.MAIN;
            selectedIndex = 3;
        }
    }

    private void loadSavedGames() {
        savedGames.clear();
        File currentDir = new File(".");
        File[] files = currentDir.listFiles((dir, name) -> name.endsWith(".bin"));
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                savedGames.add(name.substring(0, name.length() - 4)); // Remove .bin
            }
        }
    }

    private void loadGame(String playerName) {
        try {
            DataHandler dataHandler = new DataHandler();
            SaveGame saveGame = dataHandler.loadGame(playerName);
            logger.info("Loaded save game for player: {}", playerName);
            game.setScreen(new GameScreen(game, saveGame, autosaveEnabled));
        } catch (Exception e) {
            logger.error("Failed to load game: {}", e.getMessage());
            errorMessage = "Fehler beim Laden: " + e.getMessage();
        }
    }

    private void startNewGame(String playerName) {
        logger.info("Starting new game for player: {}", playerName);
        game.setScreen(new GameScreen(game, playerName, autosaveEnabled));
    }

    @Override
    public void render(float delta) {
        clearScreenBuffer();

        switch (currentState) {
            case MAIN -> renderMainMenu();
            case LOAD_GAME -> renderLoadGameMenu();
            case NEW_GAME_NAME -> renderNewGameNameInput();
            case SETTINGS -> renderSettingsMenu();
            case CREDITS -> renderCredits();
        }

        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
        batch.begin();
        drawScreenBuffer();
        batch.end();
    }

    private void renderMainMenu() {
        // Title
        drawBox(35, 5, 50, 5, "Textadventure");
        drawText("Willkommen im Dungeon!", 47, 7);

        // Menu box
        drawBox(40, 12, 40, MAIN_MENU_ITEMS.length + 4, "Hauptmenü");

        for (int i = 0; i < MAIN_MENU_ITEMS.length; i++) {
            String prefix = (i == selectedIndex) ? "> " : "  ";
            drawText(prefix + MAIN_MENU_ITEMS[i], 44, 14 + i);
        }

        // Instructions
        drawText("↑↓ Auswählen   Enter Bestätigen", 40, 32);

        // Error message
        if (!errorMessage.isEmpty()) {
            drawText(errorMessage, 40, 34);
        }
    }

    private void renderLoadGameMenu() {
        drawBox(35, 8, 50, savedGames.size() + 6, "Spiel laden");

        for (int i = 0; i < savedGames.size(); i++) {
            String prefix = (i == selectedIndex) ? "> " : "  ";
            drawText(prefix + savedGames.get(i), 39, 10 + i);
        }

        String backPrefix = (selectedIndex == savedGames.size()) ? "> " : "  ";
        drawText(backPrefix + "Zurück", 39, 10 + savedGames.size() + 1);

        drawText("↑↓ Auswählen   Enter Laden   Esc Zurück", 35, 32);
    }

    private void renderNewGameNameInput() {
        drawBox(30, 12, 60, 8, "Neues Spiel");

        drawText("Gib deinen Spielernamen ein:", 34, 14);
        drawText(playerNameInput.toString() + "_", 34, 16);

        drawText("Enter Bestätigen   Esc Abbrechen", 34, 18);
    }

    private void renderSettingsMenu() {
        drawBox(35, 10, 50, SETTINGS_ITEMS.length + 4, "Einstellungen");

        String autosaveText = SETTINGS_ITEMS[0] + (autosaveEnabled ? "An" : "Aus");
        String prefix0 = (selectedIndex == 0) ? "> " : "  ";
        drawText(prefix0 + autosaveText, 39, 12);

        String prefix1 = (selectedIndex == 1) ? "> " : "  ";
        drawText(prefix1 + SETTINGS_ITEMS[1], 39, 13);

        drawText("↑↓ Auswählen   Enter/←→ Ändern   Esc Zurück", 32, 32);
    }

    private void renderCredits() {
        drawBox(30, 8, 60, 23, "Credits");

        drawText("Textadventure", 50, 11);
        drawText("Version 1.0", 52, 13);

        drawText("Entwickelt mit:", 45, 16);
        drawText("Java 21", 50, 18);
        drawText("LibGDX 1.12.1", 50, 19);

        drawText("Entwickelt von:", 45, 22);
        drawText("Myrdn", 50, 24);

        drawText("Vielen Dank fürs Spielen!", 44, 27);

        drawText("Enter/Esc Zurück", 49, 32);
    }
}
