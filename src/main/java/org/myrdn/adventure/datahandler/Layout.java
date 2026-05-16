package org.myrdn.adventure.datahandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Layout {

    private static final Random RANDOM     = new Random();
    private static final int DOOR_DOWN     = 0b0001;
    private static final int DOOR_LEFT     = 0b0010;
    private static final int DOOR_UP       = 0b0100;
    private static final int DOOR_RIGHT    = 0b1000;

    private static final int[] DIR_DY  = { 1, -1,  0,  0};
    private static final int[] DIR_DX  = { 0,  0, -1,  1};
    private static final int[] DIR_CUR = {DOOR_DOWN, DOOR_UP,   DOOR_LEFT,  DOOR_RIGHT};
    private static final int[] DIR_NBR = {DOOR_UP,   DOOR_DOWN, DOOR_RIGHT, DOOR_LEFT};

    static public int startPosY;
    static public int startPosX;

    private final int[][] layout;
    private final int xSize;
    private final int ySize;

    public Layout(int xSize, int ySize) {
        this.xSize  = xSize;
        this.ySize  = ySize;
        this.layout = new int[ySize][xSize];
    }

    public int getXSize() { return this.xSize; }
    public int getYSize() { return this.ySize; }
    public int[][] getLayout() { return layout; }

    public void buildLayout() {
        initializeLayout();
        generateStart();
        dfsCarve(startPosY, startPosX, new boolean[ySize][xSize]);
        addExtraConnections();
    }

    private void initializeLayout() {
        for (int y = 0; y < ySize; y++) {
            Arrays.fill(layout[y], 0);
        }
    }

    private void generateStart() {
        int minX        = xSize / 4;
        int maxX        = minX + xSize / 2;
        Layout.startPosX = minX + RANDOM.nextInt(maxX - minX);
        Layout.startPosY = ySize - 1;
    }

    private void dfsCarve(int y, int x, boolean[][] visited) {
        visited[y][x] = true;

        ArrayList<Integer> order = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        Collections.shuffle(order, RANDOM);

        for (int i : order) {
            int ny = y + DIR_DY[i];
            int nx = x + DIR_DX[i];
            if (ny >= 0 && ny < ySize && nx >= 0 && nx < xSize && !visited[ny][nx]) {
                layout[y][x]   |= DIR_CUR[i];
                layout[ny][nx] |= DIR_NBR[i];
                dfsCarve(ny, nx, visited);
            }
        }
    }

    private void addExtraConnections() {
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                if (RANDOM.nextInt(5) == 0) {
                    int i  = RANDOM.nextInt(4);
                    int ny = y + DIR_DY[i];
                    int nx = x + DIR_DX[i];
                    if (ny >= 0 && ny < ySize && nx >= 0 && nx < xSize) {
                        layout[y][x]   |= DIR_CUR[i];
                        layout[ny][nx] |= DIR_NBR[i];
                    }
                }
            }
        }
    }

    public boolean isFullyConnected() {
        boolean[][] visited  = new boolean[ySize][xSize];
        int visitedRooms     = depthFirstSearch(startPosY, startPosX, visited);
        return visitedRooms == ySize * xSize;
    }

    private int depthFirstSearch(int y, int x, boolean[][] visited) {
        if (y < 0 || y >= ySize || x < 0 || x >= xSize || layout[y][x] == 0 || visited[y][x]) {
            return 0;
        }
        visited[y][x] = true;
        int count = 1;
        if ((layout[y][x] & DOOR_DOWN)  != 0) count += depthFirstSearch(y + 1, x, visited);
        if ((layout[y][x] & DOOR_LEFT)  != 0) count += depthFirstSearch(y, x - 1, visited);
        if ((layout[y][x] & DOOR_UP)    != 0) count += depthFirstSearch(y - 1, x, visited);
        if ((layout[y][x] & DOOR_RIGHT) != 0) count += depthFirstSearch(y, x + 1, visited);
        return count;
    }
}
