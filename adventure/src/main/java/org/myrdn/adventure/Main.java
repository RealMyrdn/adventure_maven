package org.myrdn.adventure;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Höhe: ");
            int ySize = scanner.nextInt();
            System.out.println();
            System.out.print("Breite: ");
            int xSize = scanner.nextInt();
            System.out.println();

            Game game = new Game(xSize, ySize, "Myrdn");
            game.init();
        }
    }
}