package org.myrdn.adventure;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {
    private final int roomType;
    private final ArrayList<GameObject> objects;
    private final String roomInfo;
    
    public Room(int roomType, ArrayList<GameObject> objects, String roomInfo) {
        this.roomType = roomType;
        this.objects = objects;
        this.roomInfo = roomInfo;
    }

    public int getRoomType() {
        return this.roomType;
    }

    public ArrayList<GameObject> getObjects() {
        return this.objects;
    }

    public String getRoomInfo() {
        return this.roomInfo;
    }
}
