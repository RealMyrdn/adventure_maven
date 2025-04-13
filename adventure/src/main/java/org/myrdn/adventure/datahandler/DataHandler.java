package org.myrdn.adventure.datahandler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DataHandler {

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
