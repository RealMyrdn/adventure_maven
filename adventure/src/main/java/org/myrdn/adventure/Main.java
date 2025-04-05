package org.myrdn.adventure;

public class Main {
    public static void main(String[] args) {
        int ySize = 5;
        int xSize = 10;
        Game game = new Game(xSize, ySize, "Myrdn");
        game.init();
        game.loop();
    }
}