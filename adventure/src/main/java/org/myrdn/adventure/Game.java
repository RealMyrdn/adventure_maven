package org.myrdn.adventure;

import java.io.IOException;

public class Game {

    private final House house;
    private final Player player;
    private Renderer renderer;

    public Game(int xSize, int ySize, String name) {
        this.house = new House(xSize, ySize);
        this.player = new Player(name ,house.getStartPosition());
        try {
            this.renderer = new Renderer();
        } catch(IOException e) {
            System.out.println("Renderer konnte nicht initialisiert werden!");
            System.out.println(e);
        }
    }

    public void init() {
        try {
            System.out.println("Initialisiere Screen");
            this.renderer.initScreen();
            this.renderer.addNewPanel();
            this.renderer.addComponentToPanel(this.house.getMap(), " Map ", 0);
            this.renderer.startGUI();
        } catch (IOException e) {
            System.out.println("Screen konnte nicht gestartet werden!");
            System.out.println(e);
        }
    }
}
