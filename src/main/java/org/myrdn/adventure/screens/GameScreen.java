package org.myrdn.adventure.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

import org.myrdn.adventure.AdventureGame;
import org.myrdn.adventure.config.GameConfig;
import org.myrdn.adventure.datahandler.DataHandler;
import org.myrdn.adventure.datahandler.Dungeon;
import org.myrdn.adventure.datahandler.Layout;
import org.myrdn.adventure.datahandler.Player;
import org.myrdn.adventure.datahandler.Room;
import org.myrdn.adventure.datahandler.SaveGame;
import org.myrdn.adventure.gamecontroller.GameController;
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

    private CommandLine commandLine;
    private InputParser inputParser;
    private TextBoxList textBoxList;
    private PlayerStatus playerStatus;
    private Generator generator;
    private GameConfig config;
    private Layout layout;
    private Map map;
    private GameController controller;

    private Player player;
    private Dungeon dungeon;

    private ArrayList<Character> inputBuffer;
    private int xSize;
    private int ySize;

    public GameScreen(AdventureGame game, String playerName, boolean autosaveEnabled) {
        super(game);
        initNewGame(playerName, autosaveEnabled);
    }

    public GameScreen(AdventureGame game, SaveGame saveGame, boolean autosaveEnabled) {
        super(game);
        initLoadedGame(saveGame, autosaveEnabled);
    }

    private void initNewGame(String playerName, boolean autosaveEnabled) {
        try {
            inputBuffer = new ArrayList<>();
            DataHandler datahandler = new DataHandler();

            config = new GameConfig();
            xSize = config.getDungeonWidth();
            ySize = config.getDungeonHeight();
            layout = new Layout(xSize, ySize);
            generator = new Generator(layout, datahandler.loadObjects(), datahandler.loadItems(), datahandler.loadEnemies());
            player = new Player(playerName, Layout.startPosX, Layout.startPosY);
            dungeon = new Dungeon(generator.getRooms());
            inputParser = new InputParser();
            SaveGame savegame = new SaveGame(player, dungeon);

            logger.info("New game initialized: dungeonSize={}x{}, playerName={}", xSize, ySize, playerName);

            initRendererComponents();

            controller = new GameController(game, textBoxList, player, dungeon,
                    datahandler, savegame, autosaveEnabled, xSize, ySize);

            textBoxList.addTextBox(40, 15, 40, 10,
                    dungeon.getRoom(Layout.startPosX, Layout.startPosY).getRoomInfo(), "Erster Eindruck");

        } catch (Exception e) {
            logger.error("Fehler beim Initialisieren des Spiels", e);
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void initLoadedGame(SaveGame saveGame, boolean autosaveEnabled) {
        try {
            inputBuffer = new ArrayList<>();
            DataHandler datahandler = new DataHandler();
            config = new GameConfig();

            player = saveGame.getPlayer();
            dungeon = saveGame.getDungeon();

            xSize = config.getDungeonWidth();
            ySize = config.getDungeonHeight();
            layout = new Layout(xSize, ySize);
            inputParser = new InputParser();

            logger.info("Loaded game for player: {}", player.getName());

            resetSingletons();
            initRendererComponentsFromRooms();

            controller = new GameController(game, textBoxList, player, dungeon,
                    datahandler, saveGame, autosaveEnabled, xSize, ySize);

            textBoxList.addTextBox(40, 15, 40, 10,
                    dungeon.getRoom(player.getX(), player.getY()).getRoomInfo(), "Willkommen zurück!");

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

    private void initRendererComponentsFromRooms() {
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
            initRendererComponentsFromRooms();
        }

        controller.setTextBoxList(textBoxList);
        textBoxList.clearList();
        textBoxList.addTextBox(40, 15, 40, 10,
                dungeon.getRoom(player.getX(), player.getY()).getRoomInfo(),
                generator != null ? "Erster Eindruck" : "Willkommen zurück!");

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                handleKeyTyped(character);
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE && textBoxList.getSize() > 0) {
                    textBoxList.removeLast();
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
                    controller.executeInstructions(instructions);
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
        controller.tick();

        clearScreenBuffer();

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
                    if (c != ' ' && !Character.isISOControl(c)) {
                        screenBuffer[bufferY][bufferX] = c;
                    }
                }
            }
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
