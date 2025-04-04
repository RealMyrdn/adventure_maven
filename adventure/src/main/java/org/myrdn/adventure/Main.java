package org.myrdn.adventure;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int ySize = scanner.nextInt();
            int xSize = scanner.nextInt();

            Game game = new Game(xSize, ySize, "Myrdn");
            game.init();
        }
    }
}