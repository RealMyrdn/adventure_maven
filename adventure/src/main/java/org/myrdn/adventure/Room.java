package org.myrdn.adventure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Room implements Serializable {
    private final int roomType;
    private final ArrayList<GameObject> objects;
    private final String roomInfo;
    private static final Random RANDOM = new Random();
    
    public Room(int roomType, ArrayList<GameObject> availabeObjects) {
        this.roomType = roomType;
        this.objects = addObjects(availabeObjects);
        this.roomInfo = generateRoomInfo();
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

    private String generateRoomInfo() {
        String info = "";
        return info;
    }

    private ArrayList<GameObject> addObjects(ArrayList<GameObject> availabeObjects) {
        if(availabeObjects != null && !availabeObjects.isEmpty()) {
            int countObjects = RANDOM.nextInt(3);
            ArrayList<GameObject> newObjects = new ArrayList<>();
            for(int i = 0; i < countObjects; i++) {
                Collections.shuffle(availabeObjects);
                newObjects.add(availabeObjects.get(0));
                availabeObjects.remove(0);
            }
            return newObjects;
        } else {
            return null;
        }
    }
}
