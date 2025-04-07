package org.myrdn.adventure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
                    if(keyStrokes.size() < 40 && keyType == KeyType.Character) {
                        keyStrokes.add(keyStroke);
                        this.renderer.printInputLine(keyStrokes);
                    }
                    if(!keyStrokes.isEmpty() && keyType == KeyType.Backspace) {
                        keyStrokes.removeLast();
                        this.renderer.printInputLine(keyStrokes);
                    }
                } 
                executeInstructions(processKeyStrokes(keyStrokes));
                keyStrokes.clear();
                this.renderer.printInputLine(keyStrokes);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public ArrayList<String> processKeyStrokes(ArrayList<KeyStroke> keyStrokes) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> instructions = new ArrayList<>();

        for(KeyStroke keyStroke : keyStrokes) {
            stringBuilder.append(keyStroke.getCharacter().toString());
        }

        String command = stringBuilder.toString().toLowerCase();
        String[] commands = command.split(" ");
        instructions.addAll(Arrays.asList(commands));
        
        return instructions;
    }

    public void executeInstructions(ArrayList<String> instructions) {
        int value = this.house.getRoom(this.player.getPosition()[1], this.player.getPosition()[0]).getRoomType();
        System.out.println(value);
        String command = instructions.getFirst();
        String target = "";
        if(instructions.size() > 1) {
            target = instructions.get(1);
        }
        if(instructions.size() > 2) {
            for(int i = 2; i < instructions.size() + 1; i++) {
                target += " " + instructions.get(i);
            }
        }
        switch (command) {
            case "exit" -> {            
                try {
                    datahandler.saveGame(savegame);
                } catch (IOException e) {
                    System.out.println(e);
                }
                System.exit(0);
            }
            case "gehe" -> {
                switch(target) {
                    case "nord" -> {
                        north(value);
                    }
                    case "süd" -> {
                        south(value);
                    }
                    case "west" -> {
                        west(value);
                    }
                    case "ost" -> {
                        east(value);
                    }
                }
            }
            case "untersuche" -> {

            }
        }
    }
    
    public void north(int value) {
        if((value & 0b0100) != 0) {
            int[] position = {this.player.getPosition()[0] - 1, this.player.getPosition()[1]};
            this.player.setPosition(position);
        }
    }

    public void south(int value) {
        if((value & 0b0001) != 0 && this.player.getPosition()[0] + 1 < this.generator.getYSize()) {
            int[] position = {this.player.getPosition()[0] + 1, this.player.getPosition()[1]};
            this.player.setPosition(position);
        }
    }

    public void west(int value) {
        if((value & 0b0010) != 0) {
            int[] position = {this.player.getPosition()[0], this.player.getPosition()[1] - 1};
            this.player.setPosition(position);
        }
    }

    public void east(int value) {
        if((value & 0b1000) != 0) {
            int[] position = {this.player.getPosition()[0], this.player.getPosition()[1] + 1};
            this.player.setPosition(position);
        }
    }
}
