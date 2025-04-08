package org.myrdn.adventure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class Game {

    private static final int NORTH = 0b0100;
    private static final int SOUTH = 0b0001;
    private static final int WEST  = 0b0010;
    private static final int EAST  = 0b1000;

    private final House house;
    private final Player player;
    private final Generator generator;
    private final SaveGame savegame;
    private DataHandler datahandler = new DataHandler();
    private Renderer renderer;

    public Game(int xSize, int ySize, String name) throws IOException  {
        this.generator    = new Generator(xSize, ySize, datahandler.loadObjects(), datahandler.loadItems());
        this.house        = new House(this.generator.getStartPosition(), this.generator.getRooms());
        this.player       = new Player(name ,house.getStartPosition());
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
            if(firstDraw) {
                this.renderer.printMap(this.house.drawMap(), player.getPosition()[1], player.getPosition()[0]);
                this.renderer.printRoomDescription(
                    this.house.getRoom(player.getPosition()[1], player.getPosition()[0]).getRoomInfo(), 
                    20
                );
                this.renderer.printInputLine(keyStrokes);
                this.renderer.renderFrame();
                firstDraw = false;
            }
            while (true) {
                keyStroke = this.renderer.getTerminal().readInput();
                keyType = keyStroke.getKeyType();

                // System.out.println("KeyType: " + keyType);
                // System.out.println("Char: " + keyStroke.getCharacter());

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
        // boolean isRunning = true;
        // try {
        //     KeyStroke keyStroke;
        //     KeyType keyType;
        //     ArrayList<KeyStroke> keyStrokes = new ArrayList<>();
        //     while(isRunning) {
        //         this.renderer.printMap(this.house.drawMap(), player.getPosition()[1], player.getPosition()[0]);
        //         this.renderer.printRoomDescription(this.house.getRoom(player.getPosition()[1], player.getPosition()[0]).getRoomInfo(), 20);
        //         while(true) {
        //             keyStroke = this.renderer.getTerminal().readInput();
        //             keyType = keyStroke.getKeyType();
        //             System.out.println("KeyType: " + keyType);
        //             System.out.println("Char: " + keyStroke.getCharacter());
        //             if(keyType == KeyType.Enter) {
        //                 break;
        //             }
        //             if(keyStrokes.size() < 40 && keyType == KeyType.Character) {
        //                 keyStrokes.add(keyStroke);
        //                 this.renderer.printInputLine(keyStrokes);
        //             }
        //             if(!keyStrokes.isEmpty() && keyType == KeyType.Backspace) {
        //                 keyStrokes.removeLast();
        //                 this.renderer.printInputLine(keyStrokes);
        //             }
        //         } 
        //         executeInstructions(processKeyStrokes(keyStrokes));
        //         keyStrokes.clear();
        //         this.renderer.printInputLine(keyStrokes);
        //     }
        // } catch (IOException e) {
        //     System.out.println(e);
        // }
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
                    case "nord" -> move(  -1, 0, NORTH, value);
                    case "süd"  -> move(1, 0, SOUTH, value);
                    case "west" -> move(0,   -1,  WEST, value);
                    case "ost"  -> move(0, 1,  EAST, value);
                }
            }
            case "untersuche" -> {
                switch(instructions.get(1)) {
                    case "raum" -> {
                        this.renderer.printDescription(this.house.getRoom(this.player.getPosition()[1], this.player.getPosition()[0]).getRoomObjects(), 5);
                        this.renderer.renderFrame();
                    }
                }
            }
        }
    }

    private void move(int dx, int dy, int directionBit, int value) {
        if ((value & directionBit) != 0) {
            int newX = this.player.getPosition()[0] + dx;
            int newY = this.player.getPosition()[1] + dy;
            this.player.setPosition(new int[] { newX, newY });
        }
    }
}
