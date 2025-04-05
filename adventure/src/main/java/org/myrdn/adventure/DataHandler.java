package org.myrdn.adventure;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataHandler {
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
