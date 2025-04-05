package org.myrdn.adventure;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int ySize = 10;
        int xSize = 20;
        try {
            Game game = new Game(xSize, ySize, "Myrdn");
            game.init();
        } catch(IOException e) {
            System.out.println("Spiel konnte nicht initialisiert werden");
            System.out.println(e);
        }
    }
}