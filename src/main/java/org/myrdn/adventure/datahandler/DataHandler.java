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
            // Format: Name; MaxUses; Anzahl; EffectType; EffectValue; Description
            String name = item[0];
            int maxUses = Integer.parseInt(item[1]);
            int count = Integer.parseInt(item[2]);
            ItemObject.EffectType effectType = ItemObject.EffectType.valueOf(item[3]);
            int effectValue = Integer.parseInt(item[4]);
            String description = item[5];

            for(int i = 0; i < count; i++) {
                itemObjects.add(new ItemObject(name, maxUses, description, effectType, effectValue));
            }
        }

        return itemObjects;

    }

    public ArrayList<Enemy> loadEnemies() throws NumberFormatException, IOException {

        ArrayList<Enemy> enemies = new ArrayList<>();
        InputStream inputStream = DataHandler.class.getResourceAsStream("/csv/enemies.csv");

        if (inputStream == null) {
            throw new IOException("enemies.csv nicht gefunden");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        while((line = reader.readLine()) != null) {
            String[] parts = line.split("; ");
            // Format: Name; Health; Attack; Defense; Anzahl; Description
            String name = parts[0];
            int health = Integer.parseInt(parts[1]);
            int attack = Integer.parseInt(parts[2]);
            int defense = Integer.parseInt(parts[3]);
            int count = Integer.parseInt(parts[4]);
            String description = parts[5];

            for(int i = 0; i < count; i++) {
                enemies.add(new Enemy(name, health, attack, defense, description));
            }
        }

        return enemies;
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
