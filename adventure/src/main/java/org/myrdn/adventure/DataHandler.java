package org.myrdn.adventure;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DataHandler {

    public ArrayList<GameObject> loadObjects() {
        ArrayList<GameObject> gameObjects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("objects.csv"))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] item = line.split("; ");
                gameObjects.add(new GameObject(Integer.parseInt(item[0]), item[1], Integer.parseInt(item[2]), Integer.parseInt(item[3]), item[4]));
            }
        } catch(IOException e) {
            System.out.println(e);
            return null;
        }
        return gameObjects;
    }

    public void saveGame(SaveGame savegame) throws IOException {
        String fileName= savegame.getPlayerName() + ".bin";
        FileOutputStream fos = new FileOutputStream(fileName);
        try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(savegame);
        }
    }

    public SaveGame loadGame(String playerName) throws IOException, ClassNotFoundException {
        String fileName= playerName + ".bin";
        FileInputStream fin = new FileInputStream(fileName);
        SaveGame savegame;
            try (ObjectInputStream ois = new ObjectInputStream(fin)) {
                savegame = (SaveGame) ois.readObject();
            }
        return savegame;
    }
}
