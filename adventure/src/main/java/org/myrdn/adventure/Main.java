package org.myrdn.adventure;

import java.io.IOException;

import org.myrdn.adventure.gamecontroller.Game;

public class Main {

    static public int ySize = 7;
    static public int xSize = 13;

    public static void main(String[] args) {
        
        try {
        
            Game game = new Game(xSize, ySize, "Myrdn");
            game.init();
        
        } catch(IOException e) {
        
            System.out.println("Virtuelle Konsole konnte nicht initialisiert werden");
            System.out.println(e);
        
        }
    
    }

}