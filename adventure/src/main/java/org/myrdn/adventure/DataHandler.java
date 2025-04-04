package org.myrdn.adventure;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataHandler {
    public static void savePlayer(Player player) throws IOException {
        String fileName= player.getName() + ".bin";
        FileOutputStream fos = new FileOutputStream(fileName);
        try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(player);
        }
    }

    public static Player loadPlayer(String playerName) throws IOException, ClassNotFoundException {
    String fileName= playerName + ".bin";
    FileInputStream fin = new FileInputStream(fileName);
    Player player;
        try (ObjectInputStream ois = new ObjectInputStream(fin)) {
            player = (Player) ois.readObject();
        }
    return player;
    }
}
