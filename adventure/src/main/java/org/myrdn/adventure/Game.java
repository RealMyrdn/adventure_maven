package org.myrdn.adventure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.myrdn.adventure.renderer.Renderer;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class Game {

    private static final int SOUTH = 0b0001;
    private static final int WEST  = 0b0010;
    private static final int NORTH = 0b0100;
    private static final int EAST  = 0b1000;

    private final House house;
    private final Player player;
    private final Generator generator;
    private final SaveGame savegame;
    private DataHandler datahandler = new DataHandler();
    private Renderer renderer;
    private int xPos;
    private int yPos;
    private ArrayList<GameObject> objects = new ArrayList<>();
    private GameObject activeObject = null;

    public Game(int xSize, int ySize, String name) throws IOException  {
        this.generator    = new Generator(xSize, ySize, datahandler.loadObjects(), datahandler.loadItems());
        this.house        = new House(this.generator.getStartPosition(), this.generator.getRooms());
        this.player       = new Player(name ,house.getStartPosition());
        this.xPos         = this.player.getPosition()[1];
        this.yPos         = this.player.getPosition()[0];
        this.savegame     = new SaveGame(this.player, this.house);
        try {
            this.renderer = new Renderer(xSize, ySize);
        } catch(IOException e) {
            System.out.println("Renderer konnte nicht initialisiert werden!");
            System.out.println(e);
        }
    }

    public void init() {
        try {
            this.renderer.initScreen();
        } catch (IOException e) {
            System.out.println("Screen konnte nicht gestartet werden!");
            System.out.println(e);
        }
        loop();
    }

    public void loop() {
        boolean isRunning = true;
        boolean firstDraw = true;

        try {
            KeyStroke keyStroke;
            KeyType keyType;
            ArrayList<KeyStroke> keyStrokes = new ArrayList<>();

            while (isRunning && this.renderer.getScreen() != null) {
                this.xPos         = this.player.getPosition()[1];
                this.yPos         = this.player.getPosition()[0];
                if(firstDraw) {
                    this.renderer.printMap(this.house.drawMap(), player.getPosition()[1], player.getPosition()[0]);
                    this.renderer.printRoomDescription(this.house.getRoom(player.getPosition()[1], player.getPosition()[0]).getRoomInfo(), 20);
                    this.renderer.printInputLine(keyStrokes);
                    this.renderer.renderFrame();
                    firstDraw = false;
                }

                while (true) {
                    keyStroke = this.renderer.getTerminal().readInput();
                    keyType = keyStroke.getKeyType();

                    if (keyType == KeyType.Enter) {
                        break;
                    }

                    if (keyStrokes.size() < 40 && keyType == KeyType.Character) {
                        keyStrokes.add(keyStroke);
                    } else if (!keyStrokes.isEmpty() && keyType == KeyType.Backspace) {
                        keyStrokes.removeLast();
                    }

                    this.renderer.printInputLine(keyStrokes);
                    this.renderer.renderFrame();
                }

                executeInstructions(processKeyStrokes(keyStrokes));
                keyStrokes.clear();

                this.renderer.printMap(this.house.drawMap(), player.getPosition()[1], player.getPosition()[0]);
                this.renderer.printRoomDescription(this.house.getRoom(player.getPosition()[1], player.getPosition()[0]).getRoomInfo(), 20);
                this.renderer.printInputLine(keyStrokes);
                this.renderer.renderFrame();
            }

        } catch (IOException e) {
            System.out.println("Fehler im Game-Loop:");
            System.out.println(e);
        }
    }

    public ArrayList<String> processKeyStrokes(ArrayList<KeyStroke> keyStrokes) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> instructions = new ArrayList<>();

        for(KeyStroke keyStroke : keyStrokes) {
            stringBuilder.append(keyStroke.getCharacter().toString());
        }

        String[] commands = stringBuilder.toString().toLowerCase().split(" ", 2);
        instructions.addAll(Arrays.asList(commands));

        return instructions;
    }

    public void executeInstructions(ArrayList<String> instructions) throws IOException {
        int value = this.house.getRoom(this.player.getPosition()[1], this.player.getPosition()[0]).getRoomType();
        System.out.println(value);
        
        switch (instructions.get(0)) {
            case "exit" -> {            
                try {
                    datahandler.saveGame(savegame);
                } catch (IOException e) {
                    System.out.println(e);
                }
                System.exit(0);
            }
            case "gehe" -> {
                switch(instructions.get(1)) {
                    case "süd"  -> move(0, 1, SOUTH, value);
                    case "west" -> move(  -1, 0,  WEST, value);
                    case "nord" -> move(0,   -1, NORTH, value);
                    case "ost"  -> move(1, 0,  EAST, value);
                }
            }
            case "untersuche" -> {
                switch(instructions.get(1)) {
                    case "raum" -> {
                        this.renderer.printDescription(this.house.getRoom(xPos, yPos).getRoomObjects(), 5);
                        this.renderer.renderFrame();
                    }

                    default -> {
                        objects = this.house.getRoom(xPos, yPos).getObjects();
                        for(GameObject object : objects) {
                            if(object.getName().toLowerCase().equals(instructions.get(1))) {
                                this.activeObject = object;
                                this.renderer.printObjectDescription(object.getDescription(), object.getHiddenItems(), 10);
                                this.renderer.renderFrame();
                            }
                        }
                        if(activeObject != null) {
                            for(ItemObject item : activeObject.getHiddenItems()) {
                                if(item.getName().toLowerCase().equals(instructions.get(1))) {
                                    // this.renderer.printItemDescription(item.getDescription(), 25);
                                }
                            }
                        }
                    }
                }
            }
            case "nimm" -> {

            }
            case "inventar" -> {

            }
            case "benutze" -> {
                
            }
        }
    }

    private void move(int dx, int dy, int directionBit, int value) {
        if ((value & directionBit) != 0) {
            int newX = this.xPos + dx;
            int newY = this.yPos + dy;
            this.player.setPosition(new int[] { newY, newX });
        }
    }
}
