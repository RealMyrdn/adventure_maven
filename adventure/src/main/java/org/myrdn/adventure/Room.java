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
        checkExits(doors, stringbuilder);
        checkObjects(stringbuilder);
        return stringbuilder.toString();
    }

    private void checkObjects(StringBuilder stringbuilder) {
        if(this.objects != null && !this.objects.isEmpty()) {
            if(this.objects.size() <= 1) {
                stringbuilder.append("Als du dich umschaust, siehst du ein Objekt, das hier herumsteht.\n");
            } else {
                stringbuilder.append("Als du dich umschaust, siehst du ").append(this.objects.size()).append(" Objekte, die hier verstreut herumstehen.\n");
            }
            for(GameObject object : this.objects) {
                stringbuilder.append(object.getName()).append("\n");
            }
        } else {
            stringbuilder.append("Hier scheint es nichts von Interesse zu geben.\n");
        }
    }

    private void checkExits(int doors, StringBuilder stringbuilder) {
        if(doors > 1) {
            stringbuilder.append("Dieser Raum hat ").append(doors).append(" Ausgänge.\n");
        } else {
            stringbuilder.append("Dieser Raum hat einen Ausgang.\n");
        }
    }

    private ArrayList<GameObject> addObjects(ArrayList<GameObject> availabeObjects) {
        if(availabeObjects != null && !availabeObjects.isEmpty()) {
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
                    availabeObjects.remove(0);
                }
            }
            return newObjects;
        } else {
            return null;
        }
    }
}
