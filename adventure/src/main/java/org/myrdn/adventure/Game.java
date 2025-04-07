package org.myrdn.adventure;

import java.io.IOException;
import java.util.ArrayList;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class Game {

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
        try {
            KeyStroke keyStroke;
            KeyType keyType;
            ArrayList<KeyStroke> keyStrokes = new ArrayList<>();
            while(isRunning) {
                this.renderer.printMap(this.house.drawMap(), player.getPosition()[1], player.getPosition()[0]);
                this.renderer.printRoomDescription(this.house.getRoom(player.getPosition()[1], player.getPosition()[0]).getRoomInfo());
                while(true) {
                    keyStroke = this.renderer.getTerminal().readInput();
                    keyType = keyStroke.getKeyType();
                    System.out.println("KeyType: " + keyType);
                    System.out.println("Char: " + keyStroke.getCharacter());
                    if(keyType == KeyType.Enter) {
                        break;
                    }
                    if(keyStrokes.size() < 40 && keyStroke.getCharacter() != null) {
                        keyStrokes.add(keyStroke);
                        this.renderer.printInputLine(keyStrokes);
                    }
                } 
                processKeyStrokes(keyStrokes);
                keyStrokes.clear();
                this.renderer.printInputLine(keyStrokes);
            }
            this.renderer.getScreen().stopScreen();
        } catch (IOException e) {
            System.out.println(e);
        }
        try {
            datahandler.saveGame(savegame);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void processKeyStrokes(ArrayList<KeyStroke> keyStrokes) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for(KeyStroke keyStroke : keyStrokes) {
            stringBuilder.append(keyStroke.getCharacter().toString());
        }

        String command = stringBuilder.toString();

        switch (command) {
            case "exit" -> this.renderer.getScreen().stopScreen();
            case "nord", "gehe nord" -> {
                int[] position = {this.player.getPosition()[0]-1, this.player.getPosition()[1]};
                this.player.setPosition(position);
            }
        }

    }
}
