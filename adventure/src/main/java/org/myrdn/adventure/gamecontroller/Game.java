package org.myrdn.adventure.gamecontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.myrdn.adventure.datahandler.DataHandler;
import org.myrdn.adventure.datahandler.GameObject;
import org.myrdn.adventure.datahandler.House;
import org.myrdn.adventure.datahandler.ItemObject;
import org.myrdn.adventure.datahandler.Layout;
import org.myrdn.adventure.datahandler.Player;
import org.myrdn.adventure.datahandler.SaveGame;
import org.myrdn.adventure.renderer.Renderer;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class Game {

    private static final int SOUTH = 0b0001;
    private static final int WEST  = 0b0010;
    private static final int NORTH = 0b0100;
    private static final int EAST  = 0b1000;

    private final Generator generator;
    private final SaveGame savegame;

    private ArrayList<GameObject> objects = new ArrayList<>();
    private DataHandler datahandler = new DataHandler();
    private GameObject activeObject = null;
    private Renderer renderer;
    private Layout layout;
    private Player player;
    private House house;
    private int xPos;
    private int yPos;

    public Game(int xSize, int ySize, String name) throws IOException  {

        this.layout       = new Layout(xSize, ySize);
        this.generator    = new Generator(this.layout, datahandler.loadObjects(), datahandler.loadItems());
        this.player       = new Player(name ,Layout.startPosX, Layout.startPosY);
        this.house        = new House(this.generator.getRooms());
        this.savegame     = new SaveGame(this.player, this.house);
        this.xPos         = Layout.startPosX;
        this.yPos         = Layout.startPosY;

        try {

            this.renderer = new Renderer(this.generator, this.player);

        } catch(IOException e) {

            System.out.println("Renderer konnte nicht initialisiert werden!");
            System.out.println(e);

        }
        
    }

    public void setHouse(House house) {

        this.house = house;

    }

    public void setPlayer(Player player) {

        this.player = player;

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

                this.xPos = this.player.getX();
                this.yPos = this.player.getY();

                if(firstDraw) {

                    this.renderer.printRoomDescription(this.house.getRoom(this.xPos, this.yPos).getRoomInfo(), 20);
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

                this.renderer.printRoomDescription(this.house.getRoom(this.player.getX(), this.player.getY()).getRoomInfo(), 20);
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

        int value = this.house.getRoom(this.player.getX(), this.player.getY()).getRoomType();
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
            this.player.setPosition(newX, newY);

        }

    }

}