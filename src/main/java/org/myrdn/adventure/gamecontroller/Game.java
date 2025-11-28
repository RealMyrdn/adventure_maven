package org.myrdn.adventure.gamecontroller;

import java.io.IOException;
import java.util.ArrayList;

import org.myrdn.adventure.config.GameConfig;
import org.myrdn.adventure.datahandler.DataHandler;
import org.myrdn.adventure.datahandler.Dungeon;
import org.myrdn.adventure.datahandler.GameObject;
import org.myrdn.adventure.datahandler.ItemObject;
import org.myrdn.adventure.datahandler.Layout;
import org.myrdn.adventure.datahandler.Player;
import org.myrdn.adventure.datahandler.Room;
import org.myrdn.adventure.datahandler.SaveGame;
import org.myrdn.adventure.renderer.CommandLine;
import org.myrdn.adventure.renderer.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * Main game controller managing the game loop, player input, and game state.
 */
public class Game {

    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    private static final int SOUTH = 0b0001;
    private static final int WEST  = 0b0010;
    private static final int NORTH = 0b0100;
    private static final int EAST  = 0b1000;

    private final CommandLine commandLine;
    private final InputParser inputParser;
    private final Generator generator;
    private final SaveGame savegame;
    private final GameConfig config;

    private DataHandler datahandler = new DataHandler();
    private GameObject activeObject = null;
    private Renderer renderer;
    private Dungeon dungeon;
    private Layout layout;
    private Player player;
    private String name;
    private int xSize;
    private int ySize;
    private int xPos;
    private int yPos;
    private long lastAutoSaveTime;

    public Game() throws IOException  {

        // Load configuration
        this.config      = new GameConfig();
        this.xSize       = config.getDungeonWidth();
        this.ySize       = config.getDungeonHeight();
        this.name        = config.getPlayerName();
        this.layout      = new Layout(this.xSize, this.ySize);
        this.generator   = new Generator(this.layout, datahandler.loadObjects(), datahandler.loadItems());
        this.player      = new Player(this.name, Layout.startPosX, Layout.startPosY);
        this.dungeon     = new Dungeon(this.generator.getRooms());
        this.inputParser = new InputParser();
        this.savegame    = new SaveGame(this.player, this.dungeon);
        this.xPos        = Layout.startPosX;
        this.yPos        = Layout.startPosY;

        logger.info("Game initialized with config: dungeonSize={}x{}, playerName={}", xSize, ySize, name);

        this.lastAutoSaveTime = System.currentTimeMillis();

        try {

            this.renderer = new Renderer(this.generator, this.player);

        } catch(IOException e) {

            logger.error("Renderer konnte nicht initialisiert werden!", e);
            throw e;

        }

        this.commandLine = CommandLine.createCommandLine();
        
    }

    public void setDungeon(Dungeon dungeon) {

        this.dungeon = dungeon;

    }

    public void setPlayer(Player player) {

        this.player = player;

    }

    /**
     * Initializes the game screen and starts the game loop.
     */
    public void init() {

        try {

            logger.info("Initializing game screen...");
            this.renderer.initScreen();
            logger.info("Game screen initialized successfully");

        } catch (IOException e) {

            logger.error("Screen konnte nicht gestartet werden!", e);
            System.err.println("Fehler beim Starten des Spiels. Siehe logs/adventure.log für Details.");
            System.exit(1);

        }

        loop();

    }

    public void loop() {

        boolean isRunning = true;

        try {

            KeyStroke keyStroke;
            KeyType keyType;
            ArrayList<KeyStroke> keyStrokes = new ArrayList<>();

            this.xPos = this.player.getX();
            this.yPos = this.player.getY();

            this.renderer.getTextBoxList().addTextBox(40, 15, 40, 10, this.dungeon.getRoom(xPos, yPos).getRoomInfo(), "Erster Eindruck");
            this.renderer.renderFrame();

            while (isRunning && this.renderer.getScreen() != null) {

                this.xPos = this.player.getX();
                this.yPos = this.player.getY();

                // Auto-save check
                checkAutoSave();

                this.renderer.getTextGraphics().fill(' ');

                keyStroke = this.renderer.getTerminal().readInput();
                keyType = keyStroke.getKeyType();
                                
                if (keyStrokes.size() < 80 && keyType == KeyType.Character) {
                
                    commandLine.addKeyStroke(keyStroke);
                    keyStrokes.add(keyStroke);
                
                } else if (!keyStrokes.isEmpty() && keyType == KeyType.Backspace) {
                
                    commandLine.removeLast();
                    keyStrokes.removeLast();
                
                } else if (!keyStrokes.isEmpty() && keyType == KeyType.Enter) {

                    executeInstructions(inputParser.processKeyStrokes(keyStrokes));
                    commandLine.resetKeyStrokes();
                    keyStrokes.clear();

                } else if(keyType == KeyType.Escape && this.renderer.getTextBoxList().getSize() > 0) {

                    this.renderer.getTextBoxList().removeLast();

                }

                this.renderer.renderFrame();

            }

            System.exit(0);

        } catch (IOException e) {

            logger.error("Fehler im Game-Loop:", e);
            System.err.println("Kritischer Fehler im Spiel. Siehe logs/adventure.log für Details.");

        }

    }

    /**
     * Executes player commands and provides feedback.
     *
     * @param instructions List of command tokens
     * @throws IOException if rendering fails
     */
    public void executeInstructions(ArrayList<String> instructions) throws IOException {

        if(instructions == null || instructions.isEmpty()) {
            showMessage("Bitte gib einen Befehl ein. Tippe 'hilfe' für Hilfe.");
            return;
        }

        String command = instructions.get(0).toLowerCase();
        Room currentRoom = dungeon.getRoom(player.getX(), player.getY());
        int roomType = currentRoom.getRoomConnections();

        logger.debug("Executing command: {}", command);

        switch(command) {

            case "exit" -> handleExit();
            case "gehe" -> handleMovement(instructions, roomType);
            case "untersuche" -> handleExamine(instructions);
            case "nimm" -> handleTake(instructions);
            case "inventar" -> handleInventory();
            case "benutze" -> handleUse(instructions);
            case "hilfe" -> handleHelp();
            case "render" -> this.renderer.renderFrame();
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
            System.err.println("Warnung: Spiel konnte nicht gespeichert werden!");

        }

        logger.info("Exiting game");
        System.exit(0);

    }

    private void handleMovement(ArrayList<String> instructions, int roomType) {

        if(instructions.size() < 2) {
            showMessage("Wohin möchtest du gehen? (nord/süd/ost/west)");
            return;
        }

        String direction = instructions.get(1).toLowerCase();

        switch(direction) {

            case "süd" -> move(0, 1, SOUTH, roomType);
            case "west" -> move(-1, 0, WEST, roomType);
            case "nord" -> move(0, -1, NORTH, roomType);
            case "ost" -> move(1, 0, EAST, roomType);
            default -> showMessage("Ungültige Richtung: '" + direction + "'. Nutze: nord, süd, ost oder west.");

        }

    }

    private void handleExamine(ArrayList<String> instructions) throws IOException {

        if(instructions.size() < 2) {
            showMessage("Was möchtest du untersuchen?");
            return;
        }

        String target = instructions.get(1).toLowerCase();
        Room room = dungeon.getRoom(xPos, yPos);

        if("raum".equals(target)) {
        
            renderer.getTextBoxList().addTextBox(25, 13, 45, 10, room.getRoomObjects(), "Entdeckungen");
            renderer.renderFrame();
        
            return;
        }

        for(GameObject object : room.getObjects()) {
           
            if(object.getName().equalsIgnoreCase(target)) {

                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append(object.getDescription()).append("\n");

                for(ItemObject item : object.getHiddenItems()) {
                    
                    stringBuilder.append(item.getName()).append("\n");
                
                }
                
                renderer.getTextBoxList().addTextBox(65, 12, 45, 15, stringBuilder.toString(), "Fund");
                renderer.renderFrame();

                activeObject = object;

                break;
            
            }
        
        }
    
    }

    private void handleTake(ArrayList<String> instructions) {

        if(instructions.size() >= 2 && this.activeObject != null) {

            for(ItemObject item : activeObject.getHiddenItems()) {

                if(item.getName().toLowerCase().equals(instructions.get(1).toLowerCase())) {

                    player.addItemInv(item);
                    activeObject.removeHiddenItem(item);
                    
                    break;

                }

            }

        }
    
    }

    private void handleInventory() {

        renderer.getTextBoxList().addTextBox(20, 10, 30, 10, player.getIventoryAsList(), "Inventar");

        safeRenderFrame();

    }

    private void handleUse(ArrayList<String> instructions) {

        if(instructions == null || instructions.size() < 2) {
            showMessage("Was möchtest du benutzen?");
            return;
        }

        String itemName = instructions.get(1).toLowerCase();

        // Check if player has the item
        ItemObject item = player.getItemFromInventory(itemName);

        if(item == null) {
            showMessage("Du hast keinen Gegenstand namens '" + itemName + "' im Inventar.");
            return;
        }

        logger.debug("Using item: {}", itemName);

        // Basic implementation - can be extended based on item types
        showMessage("Du benutzt: " + item.getName() + ". " + item.getDescription());

        // TODO: Add specific item effects based on item type
        // For example: healing potions, keys for doors, etc.

    }

    private void handleHelp() {
        StringBuilder help = new StringBuilder();
        help.append("Verfügbare Befehle:\n\n");
        help.append("gehe [richtung] - Bewege dich (nord/süd/ost/west)\n");
        help.append("untersuche [objekt/raum] - Untersuche einen Gegenstand oder Raum\n");
        help.append("nimm [gegenstand] - Nimm einen Gegenstand auf\n");
        help.append("inventar - Zeige dein Inventar\n");
        help.append("benutze [gegenstand] - Benutze einen Gegenstand aus dem Inventar\n");
        help.append("hilfe - Zeige diese Hilfe\n");
        help.append("exit - Spiel speichern und beenden\n");

        renderer.getTextBoxList().addTextBox(30, 10, 50, 20, help.toString(), "Hilfe");
        safeRenderFrame();
    }

    private void showMessage(String message) {
        renderer.getTextBoxList().addTextBox(40, 20, 40, 8, message, "Hinweis");
        safeRenderFrame();
    }

    private void move(int dx, int dy, int directionBit, int value) {

        if((value & directionBit) != 0 && this.yPos + dy < layout.getYSize()) {

            int newX = this.xPos + dx;
            int newY = this.yPos + dy;

            this.player.setPosition(newX, newY);
            
            this.xPos = this.player.getX();
            this.yPos = this.player.getY();

            this.renderer.getTextBoxList().clearList();
            this.renderer.getTextBoxList().addTextBox(40, 15, 40, 10, this.dungeon.getRoom(xPos, yPos).getRoomInfo(), "Erster Eindruck");

            safeRenderFrame();

        }

    }

    /**
     * Checks if auto-save should be performed and executes it if necessary.
     */
    private void checkAutoSave() {

        if(!config.isAutosaveEnabled()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long intervalMs = config.getAutosaveIntervalMinutes() * 60 * 1000L;

        if(currentTime - lastAutoSaveTime >= intervalMs) {
            performAutoSave();
            lastAutoSaveTime = currentTime;
        }

    }

    /**
     * Performs an automatic save of the game state.
     */
    private void performAutoSave() {
        try {
            logger.info("Performing auto-save...");
            datahandler.saveGame(savegame);
            logger.info("Auto-save completed successfully");
        } catch(IOException e) {
            logger.error("Auto-save failed", e);
        }
    }

    /**
     * Safely renders a frame, logging any errors that occur.
     */
    private void safeRenderFrame() {
        try {
            this.renderer.renderFrame();
        } catch(IOException e) {
            logger.error("Fehler beim Rendern des Frames", e);
        }
    }

}