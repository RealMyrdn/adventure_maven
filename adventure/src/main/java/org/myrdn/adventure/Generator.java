package org.myrdn.adventure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Generator implements Serializable {

    private final int xSize;
    private final int ySize;
    private final int[][] layout;
    private int startTile;
    private int[] startPosition;

    private static final int DOOR_DOWN          = 0b0001;
    private static final int DOOR_LEFT          = 0b0010;
    private static final int DOOR_UP            = 0b0100;
    private static final int DOOR_RIGHT         = 0b1000;
    private static final int MAX_ATTEMPTS       = 200000;
    private static final int MAX_ROOM_ATTEMPTS  = 10;
    private static final int DOOR_THRESHOLD     = 1;
    private static final Random RANDOM          = new Random();

    public Generator(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.layout = new int[ySize][xSize];
        buildMap();
    }

    public int[][] getLayout() {
        return this.layout;
    }

    public int[] getStartPosition() {
        return this.startPosition;
    }

    private int generateStartTile() {
        ArrayList<Integer> tiles = new ArrayList<>(Arrays.asList(3,5,7,9,11,13,15));
        Collections.shuffle(tiles);
        return tiles.get(0);
    }

    private void initializeLayout(int xSize, int ySize) {
        for(int i = 0; i < ySize; i++) {
            for(int j = 0; j < xSize; j++) {
                this.layout[i][j] = 0;
            }
        }
    }

    private void generateStart() {
        int yPos = this.ySize - 1;
        int minX = this.xSize / 4;
        int maxX = minX + this.xSize / 2;
        int xPos = minX + RANDOM.nextInt(maxX - minX);
        int[] genPos = {yPos, xPos};
        this.startPosition = genPos;
        this.layout[yPos][xPos] = this.startTile;
    }

    private void buildMap() {
        int count = 0;
        do {
            count++;
            this.startTile = generateStartTile();
            initializeLayout(this.xSize, this.ySize);
            generateStart();
            placeRooms();
            if(count > MAX_ATTEMPTS) {
                throw new RuntimeException("Es konnte keine vollverbundene Karte nach " + MAX_ATTEMPTS + " Versuchen erstellt werden.");
            }
        } while(!isFullyConnected());
    }

    private void placeRooms() {
        for(int y = this.ySize-1; y >= 0; y--) {
            for(int x = 0; x < this.xSize; x++) {
                if(this.layout[y][x] == 0) {
                    int value = 0b0000;
                    int counter = 0;
                    do {
                        counter++;
                        value = checkDoors(y, x, value);
                        this.layout[y][x] = value;
                        if(counter > MAX_ROOM_ATTEMPTS) {
                            break;
                        }
                    } while(value == 0);
                }
            }
        }
    }

    private int checkDirection(int value, int door, int nextY, int nextX, int oppositeDoor) {
        if(nextY >= 0 && nextY < this.ySize && nextX >= 0 && nextX < this.xSize && (this.layout[nextY][nextX] == 0 || (oppositeDoor & this.layout[nextY][nextX]) != 0)) {
            value |= (this.layout[nextY][nextX] == 0 && RANDOM.nextInt(2) >= DOOR_THRESHOLD) ? 0 : door;
        }
        return value;
    }

    private int checkDoors(int y, int x, int value) {
        value = checkDirection(value, DOOR_DOWN,  y + 1, x, 0b0100);
        value = checkDirection(value, DOOR_LEFT,  y, x - 1, 0b1000);
        value = checkDirection(value, DOOR_UP,    y - 1, x, 0b0001);
        value = checkDirection(value, DOOR_RIGHT, y, x + 1, 0b0010);
        return value;
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
        
        if((layout[y][x] & DOOR_DOWN)  != 0) count += depthFirstSearch(y + 1, x, visited);
        if((layout[y][x] & DOOR_LEFT)  != 0) count += depthFirstSearch(y, x - 1, visited);
        if((layout[y][x] & DOOR_UP)    != 0) count += depthFirstSearch(y - 1, x, visited);
        if((layout[y][x] & DOOR_RIGHT) != 0) count += depthFirstSearch(y, x + 1, visited);
        
        return count;
    }

}