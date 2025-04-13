package org.myrdn.adventure.gamecontroller;

import java.io.Serializable;

public final class House implements Serializable {

    private final int[] startPosition;
    private final Room[][] rooms;

    public House(int[] startPosition, Room[][] rooms) {
        this.startPosition = startPosition;
        this.rooms         = rooms;
    }

    public int[] getStartPosition() {
        return this.startPosition;
    }

    public Room getRoom(int xPos, int yPos) {
        Room room = rooms[yPos][xPos];
        return room;
    }

    public String drawMap() {
        StringBuilder stringbuilder = new StringBuilder();
        char[] symbol = {'█','╻','╸','┓','╹','┃','┛','┫','╺','┏','━','┳','┗','┣','┻','╋'};
        for(Room[] row : rooms) {
            for(Room room : row) {
                stringbuilder.append(symbol[room.getRoomType()]);
            }
            stringbuilder.append("\n");
        }
        return stringbuilder.toString();
    }
}