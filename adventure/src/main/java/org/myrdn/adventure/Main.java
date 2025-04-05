package org.myrdn.adventure;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int ySize = 10;
        int xSize = 20;
        try {
            Game game = new Game(xSize, ySize, "Myrdn");
            game.init();
            game.loop();
        } catch(IOException e) {
            System.out.println(e);
        }
    }
}