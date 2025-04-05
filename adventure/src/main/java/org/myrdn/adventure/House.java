package org.myrdn.adventure;

import java.io.Serializable;

public final class House implements Serializable {

    private final int[][] layout;
    private final int[] startPosition;
    private final Room[][] rooms;

    public House(int[] startPosition, int[][] layout) {
        this.layout = layout;
        this.startPosition = startPosition;
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
