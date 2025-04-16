package org.myrdn.adventure.gamecontroller;

import java.io.IOException;
import java.util.ArrayList;

import org.myrdn.adventure.datahandler.DataHandler;
import org.myrdn.adventure.datahandler.GameObject;
import org.myrdn.adventure.datahandler.House;
import org.myrdn.adventure.datahandler.ItemObject;
import org.myrdn.adventure.datahandler.Layout;
import org.myrdn.adventure.datahandler.Player;
import org.myrdn.adventure.datahandler.Room;
import org.myrdn.adventure.datahandler.SaveGame;
import org.myrdn.adventure.renderer.CommandLine;
import org.myrdn.adventure.renderer.Renderer;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class Game {

    private static final int SOUTH = 0b0001;
    private static final int WEST  = 0b0010;
    private static final int NORTH = 0b0100;
    private static final int EAST  = 0b1000;

    private final CommandLine commandLine;
    private final InputParser inputParser;
    private final Generator generator;
    private final SaveGame savegame;

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
        this.inputParser  = new InputParser();
        this.savegame     = new SaveGame(this.player, this.house);
        this.xPos         = Layout.startPosX;
        this.yPos         = Layout.startPosY;

        try {

            this.renderer = new Renderer(this.generator, this.player);

        } catch(IOException e) {

            System.out.println("Renderer konnte nicht initialisiert werden!");
            System.out.println(e);

        }

        this.commandLine = CommandLine.createCommandLine();
        
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

        try {

            KeyStroke keyStroke;
            KeyType keyType;
            ArrayList<KeyStroke> keyStrokes = new ArrayList<>();

            this.xPos = this.player.getX();
            this.yPos = this.player.getY();
            
            this.renderer.getTextBoxList().addTextBox(40, 15, 40, 10, this.house.getRoom(xPos, yPos).getRoomInfo(), "Erster Eindruck");
            this.renderer.renderFrame();

            while (isRunning && this.renderer.getScreen() != null) {

                this.xPos = this.player.getX();
                this.yPos = this.player.getY();
                
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

            System.out.println("Fehler im Game-Loop:");
            System.out.println(e);

        }

    }

    public void executeInstructions(ArrayList<String> instructions) throws IOException {
        
        if(instructions == null || instructions.isEmpty()) return;

        String command = instructions.get(0).toLowerCase();
        Room currentRoom = house.getRoom(player.getX(), player.getY());
        int roomType = currentRoom.getRoomType();

        switch(command) {

            case "exit" -> handleExit();
            case "gehe" -> handleMovement(instructions, roomType);
            case "untersuche" -> handleExamine(instructions);
            case "nimm" -> handleTake(instructions);
            case "inventar" -> handleInventory();
            case "benutze" -> handleUse(instructions);

        }
    }

    private void handleExit() {

        try {

            datahandler.saveGame(savegame);
        
        } catch (IOException e) {
        
            System.out.println(e);
        
        }
        
        System.exit(0);
    
    }

    private void handleMovement(ArrayList<String> instructions, int roomType) {
    
        if(instructions.size() < 2) return;
        
        String direction = instructions.get(1).toLowerCase();

        switch(direction) {
    
            case "süd" -> move(0, 1, SOUTH, roomType);
            case "west" -> move(-1, 0, WEST, roomType);
            case "nord" -> move(0, -1, NORTH, roomType);
            case "ost" -> move(1, 0, EAST, roomType);
    
        }
    
    }

    private void handleExamine(ArrayList<String> instructions) throws IOException {
    
        if(instructions.size() < 2) return;
        
        String target = instructions.get(1).toLowerCase();
        Room room = house.getRoom(xPos, yPos);

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
        
        try {
         
            renderer.renderFrame();
        
        } catch(IOException e) {

            System.out.println(e);

        }

    }

    private void handleUse(ArrayList<String> instructions) {

        if(instructions != null) {

            // TODO: GameItem Methoden zur Benutzung geben.

        }
      
    }

    private void move(int dx, int dy, int directionBit, int value) {

        if((value & directionBit) != 0 && this.yPos + dy < layout.getYSize()) {

            int newX = this.xPos + dx;
            int newY = this.yPos + dy;

            this.player.setPosition(newX, newY);
            
            this.xPos = this.player.getX();
            this.yPos = this.player.getY();
            
            this.renderer.getTextBoxList().clearList();
            this.renderer.getTextBoxList().addTextBox(40, 15, 40, 10, this.house.getRoom(xPos, yPos).getRoomInfo(), "Erster Eindruck");
            
            try {
            
                this.renderer.renderFrame();
            
            } catch(IOException e) {
            
                System.out.println(e);
            
            }

        }

    }

}