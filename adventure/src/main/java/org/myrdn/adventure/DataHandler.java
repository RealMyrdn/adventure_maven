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
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(player);
        oos.close();
    }

    public static Player loadPlayer(String playerName) throws IOException, ClassNotFoundException {
    String fileName= playerName + ".bin";
    FileInputStream fin = new FileInputStream(fileName);
    ObjectInputStream ois = new ObjectInputStream(fin);
    Player player = (Player) ois.readObject();
    ois.close();
    return player;
    }
}
