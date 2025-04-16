package org.myrdn.adventure.datahandler;

import java.io.Serializable;

public final class House implements Serializable {

    private final Room[][] rooms;

    public House(Room[][] rooms) {

        this.rooms = rooms;
    
    }

    public Room getRoom(int xPos, int yPos) {

        Room room = rooms[yPos][xPos];
        return room;
        
    }
    
}