package org.myrdn.adventure;

import java.io.Serializable;
import java.util.ArrayList;

public final class House implements Serializable {

    private final int[][] layout;
    private final int[] startPosition;
    private final Room[][] rooms;
    private final ArrayList<GameObject> availabeObjects;

    public House(int[] startPosition, int[][] layout, ArrayList<GameObject> availabeObjects) {
        this.layout = layout;
        this.startPosition = startPosition;
        this.availabeObjects = availabeObjects;
        this.rooms = placeRooms(this.layout);
    }

    public int[] getStartPosition() {
        return this.startPosition;
    }

    public int[][] getLayout() {
        return this.layout;
    }

    public Room getRoom(int xPos, int yPos) {
        Room room = rooms[yPos][xPos];
        return room;
    }

    public Room[][] placeRooms(int mapLayout[][]) {
        Room[][] genRooms = new Room[mapLayout.length][mapLayout[0].length];
        for(int y = this.layout.length - 1; y >= 0; y--) {
            for(int x = 0; x < this.layout[y].length - 1; x++) {
                genRooms[y][x] = new Room(this.layout[y][x], this.availabeObjects);
            }
        }
        return genRooms;
    }

    public String drawMap() {
        StringBuilder stringbuilder = new StringBuilder();
        char[] symbol = {'█','╻','╸','┓','╹','┃','┛','┫','╺','┏','━','┳','┗','┣','┻','╋'};
        for(int[] row : this.getLayout()) {
            for(int item : row) {
                stringbuilder.append(symbol[item]);
            }
            stringbuilder.append("\n");
        }
        return stringbuilder.toString();
    }

}
