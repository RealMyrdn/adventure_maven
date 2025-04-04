package org.myrdn.adventure;

import java.util.ArrayList;

public class Room {
    private final int roomType;
    private final int[] roomCoords;
    private final ArrayList<Integer> objects;
    private final String roomInfo;
    
    public Room(int roomType, int[] roomCoords, ArrayList<Integer> objects, String roomInfo) {
        this.roomType = roomType;
        this.roomCoords = roomCoords;
        this.objects = objects;
        this.roomInfo = roomInfo;
    }

    public int getRoomType() {
        return this.roomType;
    }

    public int[] getRoomCoords() {
        return this.roomCoords;
    }

    public ArrayList<Integer> getObjects() {
        return this.objects;
    }

    public String getRoomInfo() {
        return this.roomInfo;
    }
}
