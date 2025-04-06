package org.myrdn.adventure;

import java.io.IOException;

public class Game {

    private final House house;
    private final Player player;
    private final Generator generator;
    private final SaveGame savegame;
    private DataHandler datahandler = new DataHandler();
    private Renderer renderer;

    public Game(int xSize, int ySize, String name) throws IOException  {
        this.generator = new Generator(xSize, ySize, datahandler.loadObjects(), datahandler.loadItems());
        this.house = new House(this.generator.getStartPosition(), this.generator.getRooms());
        this.player = new Player(name ,house.getStartPosition());
        this.savegame = new SaveGame(this.player, this.house);
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
            this.renderer.printMap(this.house.drawMap(), player.getPosition()[1], player.getPosition()[0]);
        } catch (IOException e) {
            System.out.println("Screen konnte nicht gestartet werden!");
            System.out.println(e);
        }
        loop();
    }

    public void loop() {
        try {
            this.renderer.printMap(this.house.drawMap(), player.getPosition()[1], player.getPosition()[0]);
            this.renderer.printRoomDescription(this.house.getRoom(player.getPosition()[1], player.getPosition()[0]).getRoomInfo());
            this.renderer.getTerminal().readInput();
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
}
