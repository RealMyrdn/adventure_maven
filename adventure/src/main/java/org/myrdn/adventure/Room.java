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
        StringBuilder stringbuilder = new StringBuilder();
        int doors = Integer.bitCount(roomType);
        if(doors > 1) {
            stringbuilder.append("Dieser Raum enthält ").append(doors).append(" Ausgänge.");
        } else {
            stringbuilder.append("Dieser Raum hat einen Ausgang.");
        }
        return stringbuilder.toString();
    }

    private ArrayList<GameObject> addObjects(ArrayList<GameObject> availabeObjects) {
        if(!availabeObjects.isEmpty()) {
            int chance = RANDOM.nextInt(10);
            int countObjects = RANDOM.nextInt(2);
            ArrayList<GameObject> newObjects = new ArrayList<>();
            if(chance >= 4) {
                for(int i = 0; i <= countObjects; i++) {
                    if(availabeObjects.isEmpty()) {
                        break;
                    }
                    Collections.shuffle(availabeObjects);
                    newObjects.add(availabeObjects.get(0));
                    System.out.println(availabeObjects.get(0).getName());
                    availabeObjects.remove(0);
                }
            }
            return newObjects;
        } else {
            return null;
        }
    }
}
