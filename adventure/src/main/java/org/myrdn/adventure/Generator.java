package org.myrdn.adventure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Generator {

    private final int xSize;
    private final int ySize;
    private final int[][] layout;
    private int startTile;
    private int[] startPosition;
    private static final int DOOR_DOWN  = 0b0001;
    private static final int DOOR_LEFT  = 0b0010;
    private static final int DOOR_UP    = 0b0100;
    private static final int DOOR_RIGHT = 0b1000;

    public Generator(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.layout = new int[ySize][xSize];
        buildMap();
    }

    private void buildMap() {
        int count = 0;
        do {
            count++;
            this.startTile = generateStartTile();
            generateLayout(this.xSize, this.ySize);
            generateStart();
            placeRooms();
            if(count > 1000) {
                System.exit(0);
            }
        } while(!isFullyConnected());
    }

    private int generateStartTile() {
        ArrayList<Integer> tiles = new ArrayList<>(Arrays.asList(3,5,7,9,11,13,15));
        Collections.shuffle(tiles);
        return tiles.get(0);
    }

    private void generateLayout(int xSize, int ySize) {
        for(int i = 0; i < ySize; i++) {
            for(int j = 0; j < xSize; j++) {
                this.layout[i][j] = 0;
            }
        }
    }

    private void generateStart() {
        int yPos = this.ySize - 1;
        Random random = new Random();
        int minX = this.xSize / 4;
        int maxX = minX + this.xSize / 2;
        int xPos = minX + random.nextInt(maxX - minX);
        int[] genPos = {yPos, xPos};
        this.startPosition = genPos;
        this.layout[yPos][xPos] = this.startTile;
    }

    private int checkDown(int y, int x, int value) {
        if(y < this.ySize - 1 && (this.layout[y + 1][x] == 0 || (0b0100 & this.layout[y + 1][x]) != 0 )) {
            value |= (this.layout[y + 1][x] == 0 && Math.round(Math.random() * 10) >= 7) ? 0 : DOOR_DOWN;
        }
        return value;
    }

    private int checkLeft(int y, int x, int value) {
        if(x > 0 && (this.layout[y][x - 1] == 0 || (0b1000 & this.layout[y][x - 1]) != 0)) {
            value |= (this.layout[y][x - 1] == 0 && Math.round(Math.random() * 10) >= 7) ? 0 : DOOR_LEFT;
        }
        return value;
    }

    private int checkUp(int y, int x, int value) {
        if(y > 0 && (this.layout[y - 1][x] == 0  || (0b0001 & this.layout[y - 1][x]) != 0 )) {
            value |= (this.layout[y - 1][x] == 0 && Math.round(Math.random() * 10) >= 7) ? 0 : DOOR_UP;
        }
        return value;
    }

    private int checkRight(int y, int x, int value) {
        if(x < this.xSize - 1 && (this.layout[y][x + 1] == 0 || (0b0010 & this.layout[y][x + 1]) != 0)) {
            value |= (this.layout[y][x + 1] == 0 && Math.round(Math.random() * 10) >= 7) ? 0 : DOOR_RIGHT;
        }
        return value;
    }

    private void placeRooms() {
        for(int y = this.ySize-1; y >= 0; y--) {
            for(int x = 0; x < this.xSize; x++) {
                if(this.layout[y][x] == 0) {
                    int value = 0b0000;
                    int counter = 0;
                    do {
                        counter++;
                        value = checkDown(y,x,checkLeft(y,x,checkUp(y,x,checkRight(y,x,value))));
                        this.layout[y][x] = value;
                        if(counter > 10) {
                            break;
                        }
                    } while(value == 0);
                }
            }
        }
    }

    public int[][] getLayout() {
        return this.layout;
    }

    public int[] getStartPosition() {
        return this.startPosition;
    }

    public boolean isFullyConnected() {
        boolean[][] visited = new boolean[ySize][xSize];
        int totalRooms = ySize * xSize;
        int visitedRooms = depthFirstSearch(startPosition[0], startPosition[1], visited);
        return visitedRooms == totalRooms;
    }

    private int depthFirstSearch(int y, int x, boolean[][] visited) {
        if(y < 0 || y >= ySize || x < 0 || x >= xSize || layout[y][x] == 0 || visited[y][x]) {
            return 0;
        }
        
        visited[y][x] = true;
        int count = 1;
        
        if((layout[y][x] & DOOR_DOWN) != 0) count += depthFirstSearch(y + 1, x, visited);
        if((layout[y][x] & DOOR_LEFT) != 0) count += depthFirstSearch(y, x - 1, visited);
        if((layout[y][x] & DOOR_UP) != 0) count += depthFirstSearch(y - 1, x, visited);
        if((layout[y][x] & DOOR_RIGHT) != 0) count += depthFirstSearch(y, x + 1, visited);
        
        return count;
    }

}
