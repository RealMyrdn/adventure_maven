package org.myrdn.adventure;

import java.io.IOException;

public class Game {

    private final House house;
    private final Player player;
    private final Generator generator;
    private final SaveGame savegame;
    private DataHandler datahandler = new DataHandler();
    private Renderer renderer;

    public Game(int xSize, int ySize, String name) {
        this.generator = new Generator(xSize, ySize);
        this.house = new House(this.generator.getStartPosition(), this.generator.getLayout());
        this.player = new Player(name ,house.getStartPosition());
        this.savegame = new SaveGame(this.player, this.house);
        try {
            this.renderer = new Renderer();
        } catch(IOException e) {
            System.out.println("Renderer konnte nicht initialisiert werden!");
            System.out.println(e);
        }
    }

    public void init() {
        try {
            this.renderer.initScreen();
            this.renderer.addNewPanel();
            this.renderer.addComponentToPanel(this.house.drawMap(), " Map ", 0);
            this.renderer.startGUI();
        } catch (IOException e) {
            System.out.println("Screen konnte nicht gestartet werden!");
            System.out.println(e);
        }
        try {
            datahandler.saveGame(savegame);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public House getHouse() {
        return this.house;
    }
}
