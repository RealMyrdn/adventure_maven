package org.myrdn.adventure;

public class Main {
    public static void main(String[] args) {
        int ySize = 10;
        int xSize = 20;
        Game game = new Game(xSize, ySize, "Myrdn");
        game.init();
    }
}