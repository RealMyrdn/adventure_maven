package org.myrdn.adventure;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DataHandler {

    private static final Random RANDOM = new Random();

    public ArrayList<GameObject> generateItemObjects() throws NumberFormatException, IOException {
        ArrayList <ItemObject> availableItems = loadItems();
        ArrayList <GameObject> availableObjects = loadObjects();
        Collections.shuffle(availableItems);
        ArrayList<GameObject> newObjects = new ArrayList<>();
        for(GameObject emptyObject : availableObjects) {
            int maxStashes = emptyObject.getHiddenStashes();
            int stashesUsed = 0;
            int chance = RANDOM.nextInt(10);
            for(int i = 0; i < maxStashes; i++) {
                if(chance >= 6) {
                    if(availableItems.isEmpty()) {
                            break;
                    }
                    emptyObject.addtHiddenItem(availableItems.get(0));
                    availableItems.remove(0);
                    stashesUsed++;
                }
            }
            emptyObject.setHiddenStashes(maxStashes - stashesUsed);
            newObjects.add(emptyObject);
        }
        System.out.println("Verbleibende Items: " + availableItems.size());
        return newObjects;
    }

    public ArrayList<GameObject> loadObjects() throws NumberFormatException, IOException {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        InputStream inputStream = DataHandler.class.getResourceAsStream("/csv/objects.csv");
        if (inputStream == null) {
            throw new IOException("CSV-Datei nicht gefunden");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = reader.readLine()) != null) {
            String[] item = line.split("; ");
            for(int i = 0; i < Integer.parseInt(item[2]); i++) {
                gameObjects.add(new GameObject(item[0], Integer.parseInt(item[1]), item[3]));
            }
        }
        return gameObjects;
    }

    public ArrayList<ItemObject> loadItems() throws NumberFormatException, IOException {
        ArrayList<ItemObject> itemObjects = new ArrayList<>();
        InputStream inputStream = DataHandler.class.getResourceAsStream("/csv/items.csv");
        if (inputStream == null) {
            throw new IOException("CSV-Datei nicht gefunden");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = reader.readLine()) != null) {
            String[] item = line.split("; ");
            for(int i = 0; i < Integer.parseInt(item[2]); i++) {
                itemObjects.add(new ItemObject(item[0], Integer.parseInt(item[1]), item[3]));
            }
        }
        return itemObjects;
    }

    public void saveGame(SaveGame savegame) throws IOException {
        String fileName= savegame.getPlayerName() + ".bin";
        FileOutputStream fos = new FileOutputStream(fileName);
        try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(savegame);
        }
    }

    public SaveGame loadGame(String playerName) throws IOException, ClassNotFoundException {
        String fileName = playerName + ".bin";
        FileInputStream fin = new FileInputStream(fileName);
        SaveGame savegame;
            try (ObjectInputStream ois = new ObjectInputStream(fin)) {
                savegame = (SaveGame) ois.readObject();
            }
        return savegame;
    }
}
