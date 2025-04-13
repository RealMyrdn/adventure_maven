package org.myrdn.adventure;

import java.io.IOException;

import org.myrdn.adventure.gamecontroller.Game;

public class Main {

    public static void main(String[] args) {
        
        int ySize = 10;
        int xSize = 20;
        
        try {
        
            Game game = new Game(xSize, ySize, "Myrdn");
            game.init();
        
        } catch(IOException e) {
        
            System.out.println("Virtuelle Konsole konnte nicht initialisiert werden");
            System.out.println(e);
        
        }
    
    }

}