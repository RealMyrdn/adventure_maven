package org.myrdn.adventure.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

import org.myrdn.adventure.AdventureGame;
import org.myrdn.adventure.config.GameConfig;
import org.myrdn.adventure.datahandler.DataHandler;
import org.myrdn.adventure.datahandler.Dungeon;
import org.myrdn.adventure.datahandler.GameObject;
import org.myrdn.adventure.datahandler.ItemObject;
import org.myrdn.adventure.datahandler.Layout;
import org.myrdn.adventure.datahandler.Player;
import org.myrdn.adventure.datahandler.Room;
import org.myrdn.adventure.datahandler.SaveGame;
import org.myrdn.adventure.gamecontroller.Generator;
import org.myrdn.adventure.gamecontroller.InputParser;
import org.myrdn.adventure.renderer.CommandLine;
import org.myrdn.adventure.renderer.Map;
import org.myrdn.adventure.renderer.PlayerStatus;
import org.myrdn.adventure.renderer.TextBoxList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class GameScreen extends BaseScreen {

    private static final Logger logger = LoggerFactory.getLogger(GameScreen.class);

    private static final int SOUTH = 0b0001;
    private static final int WEST  = 0b0010;
    private static final int NORTH = 0b0100;
    private static final int EAST  = 0b1000;

    private CommandLine commandLine;
    private InputParser inputParser;
    private TextBoxList textBoxList;
    private PlayerStatus playerStatus;
    private Generator generator;
    private SaveGame savegame;
    private GameConfig config;
    private DataHandler datahandler;
    private GameObject activeObject;
    private Dungeon dungeon;
    private Layout layout;
    private Player player;
    private Map map;

    private ArrayList<Character> inputBuffer;
    private int xSize;
    private int ySize;
    private int xPos;
    private int yPos;
    private long lastAutoSaveTime;
    private boolean autosaveEnabled;

    // Constructor for new game
    public GameScreen(AdventureGame game, String playerName, boolean autosaveEnabled) {
        super(game);
        this.autosaveEnabled = autosaveEnabled;
        initNewGame(playerName);
    }

    // Constructor for loading saved game
    public GameScreen(AdventureGame game, SaveGame saveGame, boolean autosaveEnabled) {
        super(game);
        this.autosaveEnabled = autosaveEnabled;
        initLoadedGame(saveGame);
    }

    private void initNewGame(String playerName) {
        try {
            inputBuffer = new ArrayList<>();
            datahandler = new DataHandler();
            config = new GameConfig();
            xSize = config.getDungeonWidth();
            ySize = config.getDungeonHeight();
            layout = new Layout(xSize, ySize);
            generator = new Generator(layout, datahandler.loadObjects(), datahandler.loadItems());
            player = new Player(playerName, Layout.startPosX, Layout.startPosY);
            dungeon = new Dungeon(generator.getRooms());
            inputParser = new InputParser();
            savegame = new SaveGame(player, dungeon);
            xPos = Layout.startPosX;
            yPos = Layout.startPosY;

            logger.info("New game initialized: dungeonSize={}x{}, playerName={}", xSize, ySize, playerName);

            lastAutoSaveTime = System.currentTimeMillis();

            initRendererComponents();

            textBoxList.addTextBox(40, 15, 40, 10,
                    dungeon.getRoom(xPos, yPos).getRoomInfo(), "Erster Eindruck");

        } catch (IOException e) {
            logger.error("Fehler beim Initialisieren des Spiels", e);
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void initLoadedGame(SaveGame saveGame) {
        try {
            inputBuffer = new ArrayList<>();
            datahandler = new DataHandler();
            config = new GameConfig();

            player = saveGame.getPlayer();
            dungeon = saveGame.getDungeon();
            savegame = saveGame;

            xSize = config.getDungeonWidth();
            ySize = config.getDungeonHeight();
            layout = new Layout(xSize, ySize);
            inputParser = new InputParser();

            xPos = player.getX();
            yPos = player.getY();

            logger.info("Loaded game for player: {}", player.getName());

            lastAutoSaveTime = System.currentTimeMillis();

            initRendererComponentsForLoadedGame();

            textBoxList.addTextBox(40, 15, 40, 10,
                    dungeon.getRoom(xPos, yPos).getRoomInfo(), "Willkommen zurück!");

        } catch (Exception e) {
            logger.error("Fehler beim Laden des Spielstands", e);
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void initRendererComponents() {
        map = Map.createMap(generator.getXSize(), generator.getYSize(),
                player, generator.getRooms(), 1, 1);
        textBoxList = TextBoxList.createTextBoxList();
        commandLine = CommandLine.createCommandLine();
        playerStatus = PlayerStatus.createPlayerStatus(player);
    }

    private void initRendererComponentsForLoadedGame() {
        // Reset singletons for loaded game
        resetSingletons();

        Room[][] rooms = new Room[ySize][xSize];
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                rooms[y][x] = dungeon.getRoom(x, y);
            }
        }

        map = Map.createMap(xSize, ySize, player, rooms, 1, 1);
        textBoxList = TextBoxList.createTextBoxList();
        commandLine = CommandLine.createCommandLine();
        playerStatus = PlayerStatus.createPlayerStatus(player);
    }

    private void resetSingletons() {
        // Reset singleton instances for fresh game
        try {
            java.lang.reflect.Field mapField = Map.class.getDeclaredField("map");
            mapField.setAccessible(true);
            mapField.set(null, null);

            java.lang.reflect.Field textBoxListField = TextBoxList.class.getDeclaredField("textBoxList");
            textBoxListField.setAccessible(true);
            textBoxListField.set(null, null);

            java.lang.reflect.Field commandLineField = CommandLine.class.getDeclaredField("commandLine");
            commandLineField.setAccessible(true);
            commandLineField.set(null, null);

            java.lang.reflect.Field playerStatusField = PlayerStatus.class.getDeclaredField("playerStatus");
            playerStatusField.setAccessible(true);
            playerStatusField.set(null, null);
        } catch (Exception e) {
            logger.warn("Could not reset singletons: {}", e.getMessage());
        }
    }

    @Override
    public void show() {
        resetSingletons();
        if (generator != null) {
            initRendererComponents();
        } else {
            initRendererComponentsForLoadedGame();
        }

        textBoxList.clearList();
        textBoxList.addTextBox(40, 15, 40, 10,
                dungeon.getRoom(xPos, yPos).getRoomInfo(),
                generator != null ? "Erster Eindruck" : "Willkommen zurück!");

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                handleKeyTyped(character);
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    if (textBoxList.getSize() > 0) {
                        textBoxList.removeLast();
                    }
                }
                return true;
            }
        });
    }

    private void handleKeyTyped(char character) {
        if (character == '\b') {
            if (!inputBuffer.isEmpty()) {
                inputBuffer.remove(inputBuffer.size() - 1);
                commandLine.removeLast();
            }
        } else if (character == '\r' || character == '\n') {
            if (!inputBuffer.isEmpty()) {
                try {
                    ArrayList<String> instructions = inputParser.processString(getInputString());
                    executeInstructions(instructions);
                } catch (IOException e) {
                    logger.error("Fehler bei der Befehlsausführung", e);
                }
                inputBuffer.clear();
                commandLine.resetKeyStrokes();
            }
        } else if (!Character.isISOControl(character) && inputBuffer.size() < 80) {
            inputBuffer.add(character);
            commandLine.addCharacter(character);
        }
    }

    private String getInputString() {
        StringBuilder sb = new StringBuilder();
        for (Character c : inputBuffer) {
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    public void render(float delta) {
        if (autosaveEnabled) {
            checkAutoSave();
        }

        clearScreenBuffer();

        xPos = player.getX();
        yPos = player.getY();

        renderToBuffer(map.update());
        renderToBuffer(playerStatus.update());
        renderToBuffer(textBoxList.update());
        renderToBuffer(commandLine.update());

        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
        batch.begin();
        drawScreenBuffer();
        batch.end();
    }

    private void renderToBuffer(ArrayList<Object> renderObject) {
        if (renderObject == null || renderObject.isEmpty()) {
            return;
        }

        int canvasPosX = (int) renderObject.get(0);
        int canvasPosY = (int) renderObject.get(1);
        char[][] content = (char[][]) renderObject.get(2);

        if (content == null) {
            return;
        }

        for (int y = 0; y < content.length; y++) {
            for (int x = 0; x < content[y].length; x++) {
                int bufferX = x + canvasPosX;
                int bufferY = y + canvasPosY;

                if (bufferX >= 0 && bufferX < TERMINAL_COLS &&
                    bufferY >= 0 && bufferY < TERMINAL_ROWS) {

                    char c = content[y][x];
                    if (c != '\u0000' && !Character.isISOControl(c)) {
                        screenBuffer[bufferY][bufferX] = c;
                    }
                }
            }
        }
    }

    public void executeInstructions(ArrayList<String> instructions) throws IOException {
        if (instructions == null || instructions.isEmpty()) {
            showMessage("Bitte gib einen Befehl ein. Tippe 'hilfe' für Hilfe.");
            return;
        }

        String command = instructions.get(0).toLowerCase();
        Room currentRoom = dungeon.getRoom(player.getX(), player.getY());
        int roomType = currentRoom.getRoomConnections();

        logger.debug("Executing command: {}", command);

        switch (command) {
            case "exit", "beenden" -> handleExit();
            case "menu", "menü" -> returnToMenu();
            case "gehe" -> handleMovement(instructions, roomType);
            case "untersuche" -> handleExamine(instructions);
            case "nimm" -> handleTake(instructions);
            case "inventar" -> handleInventory();
            case "benutze" -> handleUse(instructions);
            case "speichern" -> handleSave();
            case "hilfe" -> handleHelp();
            default -> showMessage("Unbekannter Befehl: '" + command + "'. Tippe 'hilfe' für verfügbare Befehle.");
        }
    }

    private void handleExit() {
        try {
            logger.info("Saving game...");
            datahandler.saveGame(savegame);
            logger.info("Game saved successfully");
        } catch (IOException e) {
            logger.error("Fehler beim Speichern des Spiels", e);
        }
        logger.info("Exiting game");
        Gdx.app.exit();
    }

    private void returnToMenu() {
        try {
            logger.info("Saving game before returning to menu...");
            datahandler.saveGame(savegame);
            logger.info("Game saved successfully");
        } catch (IOException e) {
            logger.error("Fehler beim Speichern des Spiels", e);
        }
        game.setScreen(new MainMenuScreen(game));
    }

    private void handleSave() {
        try {
            datahandler.saveGame(savegame);
            showMessage("Spiel wurde gespeichert!");
            logger.info("Game saved successfully");
        } catch (IOException e) {
            logger.error("Fehler beim Speichern", e);
            showMessage("Fehler beim Speichern!");
        }
    }

    private void handleMovement(ArrayList<String> instructions, int roomType) {
        if (instructions.size() < 2) {
            showMessage("Wohin möchtest du gehen? (nord/süd/ost/west)");
            return;
        }

        String direction = instructions.get(1).toLowerCase();

        switch (direction) {
            case "süd", "s" -> move(0, 1, SOUTH, roomType);
            case "west", "w" -> move(-1, 0, WEST, roomType);
            case "nord", "n" -> move(0, -1, NORTH, roomType);
            case "ost", "o" -> move(1, 0, EAST, roomType);
            default -> showMessage("Ungültige Richtung: '" + direction + "'. Nutze: nord, süd, ost oder west.");
        }
    }

    private void handleExamine(ArrayList<String> instructions) {
        if (instructions.size() < 2) {
            showMessage("Was möchtest du untersuchen?");
            return;
        }

        String target = instructions.get(1).toLowerCase();
        Room room = dungeon.getRoom(xPos, yPos);

        if ("raum".equals(target)) {
            textBoxList.addTextBox(25, 13, 45, 10, room.getRoomObjects(), "Entdeckungen");
            return;
        }

        for (GameObject object : room.getObjects()) {
            if (object.getName().toLowerCase().contains(target)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(object.getDescription()).append("\n");

                for (ItemObject item : object.getHiddenItems()) {
                    stringBuilder.append(item.getName()).append("\n");
                }

                textBoxList.addTextBox(65, 12, 45, 15, stringBuilder.toString(), "Fund");
                activeObject = object;
                return;
            }
        }

        showMessage("'" + target + "' nicht gefunden. Versuche 'untersuche raum'.");
    }

    private void handleTake(ArrayList<String> instructions) {
        if (instructions.size() < 2) {
            showMessage("Was möchtest du nehmen?");
            return;
        }

        if (activeObject == null) {
            showMessage("Untersuche zuerst ein Objekt.");
            return;
        }

        String itemName = instructions.get(1).toLowerCase();
        ItemObject foundItem = null;

        for (ItemObject item : activeObject.getHiddenItems()) {
            if (item.getName().toLowerCase().contains(itemName)) {
                foundItem = item;
                break;
            }
        }

        if (foundItem == null) {
            showMessage("'" + instructions.get(1) + "' ist hier nicht zu finden.");
            return;
        }

        player.addItemInv(foundItem);
        activeObject.removeHiddenItem(foundItem);

        textBoxList.removeLast();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(activeObject.getDescription()).append("\n");

        if (activeObject.getHiddenItems().isEmpty()) {
            stringBuilder.append("\nHier ist nichts mehr.");
        } else {
            for (ItemObject item : activeObject.getHiddenItems()) {
                stringBuilder.append(item.getName()).append("\n");
            }
        }

        textBoxList.addTextBox(65, 12, 45, 15, stringBuilder.toString(), "Fund");
        showMessage("Du hast '" + foundItem.getName() + "' aufgenommen.");
    }

    private void handleInventory() {
        textBoxList.addTextBox(20, 10, 30, 10, player.getIventoryAsList(), "Inventar");
    }

    private void handleUse(ArrayList<String> instructions) {
        if (instructions == null || instructions.size() < 2) {
            showMessage("Was möchtest du benutzen?");
            return;
        }

        String itemName = instructions.get(1).toLowerCase();
        ItemObject item = player.getItemFromInventory(itemName);

        if (item == null) {
            showMessage("Du hast keinen Gegenstand namens '" + itemName + "' im Inventar.");
            return;
        }

        logger.debug("Using item: {}", itemName);
        showMessage("Du benutzt: " + item.getName() + ". " + item.getDescription());
    }

    private void handleHelp() {
        StringBuilder help = new StringBuilder();
        help.append("Verfügbare Befehle:\n\n");
        help.append("gehe [richtung] - Bewege dich\n");
        help.append("untersuche [objekt/raum] - Untersuche\n");
        help.append("nimm [gegenstand] - Aufnehmen\n");
        help.append("inventar - Dein Inventar\n");
        help.append("benutze [gegenstand] - Benutzen\n");
        help.append("speichern - Spiel speichern\n");
        help.append("menü - Zurück zum Hauptmenü\n");
        help.append("hilfe - Diese Hilfe\n");
        help.append("exit - Beenden\n");

        textBoxList.addTextBox(30, 8, 50, 22, help.toString(), "Hilfe");
    }

    private void showMessage(String message) {
        textBoxList.addTextBox(40, 20, 40, 8, message, "Hinweis");
    }

    private void move(int dx, int dy, int directionBit, int value) {
        if ((value & directionBit) != 0) {
            int newX = xPos + dx;
            int newY = yPos + dy;

            if (newY >= 0 && newY < ySize && newX >= 0 && newX < xSize) {
                player.setPosition(newX, newY);

                xPos = player.getX();
                yPos = player.getY();

                textBoxList.clearList();
                textBoxList.addTextBox(40, 15, 40, 10,
                        dungeon.getRoom(xPos, yPos).getRoomInfo(), "Neuer Raum");

                activeObject = null;
            }
        } else {
            showMessage("In diese Richtung gibt es keinen Ausgang.");
        }
    }

    private void checkAutoSave() {
        long currentTime = System.currentTimeMillis();
        long intervalMs = 5 * 60 * 1000L; // 5 minutes

        if (currentTime - lastAutoSaveTime >= intervalMs) {
            performAutoSave();
            lastAutoSaveTime = currentTime;
        }
    }

    private void performAutoSave() {
        try {
            logger.info("Performing auto-save...");
            datahandler.saveGame(savegame);
            logger.info("Auto-save completed successfully");
        } catch (IOException e) {
            logger.error("Auto-save failed", e);
        }
    }

    @Override
    public void hide() {
        resetSingletons();
    }

    @Override
    public void dispose() {
        resetSingletons();
    }
}
