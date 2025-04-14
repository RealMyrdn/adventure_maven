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
            
            this.renderer.getTextBoxList().addTextBox(40, 15, 40, 10, this.house.getRoom(xPos, yPos).getRoomInfo(), "Info");
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

                    executeInstructions(processKeyStrokes(keyStrokes));
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
            case "render" -> this.renderer.renderFrame();

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
        
            renderer.getTextBoxList().addTextBox(25, 13, 30, 10, room.getRoomObjects(), "Info");
            renderer.renderFrame();
        
            return;
        }

        for(GameObject object : room.getObjects()) {
           
            if(object.getName().equalsIgnoreCase(target)) {
                
                activeObject = object;
                renderer.printObjectDescription(object.getDescription(), object.getHiddenItems(), 10);
                renderer.renderFrame();

                for(ItemObject item : object.getHiddenItems()) {
                    
                    if(item.getName().equalsIgnoreCase(target)) {
                      
                        // renderer.printItemDescription(item.getDescription(), 25);
                    
                    }
                
                }

                break;
            
            }
        
        }
    
    }

    private void handleTake(ArrayList<String> instructions) {
    
    }

    private void handleInventory() {
    
    }

    private void handleUse(ArrayList<String> instructions) {
      
    }

    private void move(int dx, int dy, int directionBit, int value) {

        if((value & directionBit) != 0 && this.yPos + dy < layout.getYSize()) {

            int newX = this.xPos + dx;
            int newY = this.yPos + dy;

            this.player.setPosition(newX, newY);
            
            this.xPos = this.player.getX();
            this.yPos = this.player.getY();
            
            this.renderer.getTextBoxList().clearList();
            this.renderer.getTextBoxList().addTextBox(40, 15, 40, 10, this.house.getRoom(xPos, yPos).getRoomInfo(), "Info");
            
            try {
            
                this.renderer.renderFrame();
            
            } catch(IOException e) {
            
                System.out.println(e);
            
            }

        }

    }

}