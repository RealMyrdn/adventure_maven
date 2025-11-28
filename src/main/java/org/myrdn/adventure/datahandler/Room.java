package org.myrdn.adventure.datahandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Room implements Serializable {
    
    private final int roomType;
    private final int roomConnections;
    private final String roomInfo;
    private final int[][] roomLayout;
    private final ArrayList<GameObject> objects;
    
    private static final Random RANDOM = new Random();
    
    public Room(int roomConnections, ArrayList<GameObject> availableObjects) {

        this.roomConnections = roomConnections;
        this.roomLayout      = new int[18][32];
        this.roomType        = 0;
        this.roomInfo        = generateRoomInfo();
        this.objects         = addObjects(availableObjects);
    
    }

    public int getRoomConnections() {
    
        return this.roomConnections;
    
    }

    public ArrayList<GameObject> getObjects() {
    
        return this.objects;
    
    }

    public int getRoomType() {

        return this.roomType;
        
    }

    public String getRoomInfo() {
    
        return this.roomInfo;
    
    }

    public int[][] getRoomLayout() {

        return this.roomLayout;

    }

    private String generateRoomInfo() {
    
        StringBuilder stringbuilder = new StringBuilder();
        int doors = Integer.bitCount(roomConnections);

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
    
        } else {

            stringbuilder.append("Hier scheint es nichts von Interesse zu geben.\n");

        }
    
    }

    public String getRoomObjects() {

        StringBuilder stringbuilder = new StringBuilder();
    
        if(!this.objects.isEmpty()) {
    
            stringbuilder.append("In diesem Raum sind folgende Objekte: \n");
    
        } else {
    
            stringbuilder.append("In diesem Raum ist nichts von Interesse.");
    
        }
    
        for(GameObject object : this.objects) {
    
            stringbuilder.append(object.getName()).append("\n");
    
        }
        
        return stringbuilder.toString();
    
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