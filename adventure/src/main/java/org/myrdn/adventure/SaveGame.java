package org.myrdn.adventure;

import java.io.Serializable;

public class SaveGame implements Serializable {
    private final Player player;
    private final House house;
    public SaveGame(Player player, House house) {
        this.player = player;
        this.house = house;
    }

    public String getPlayerName() {
        return this.player.getName();
    }

    public House getHouse() {
        return this.house;
    }
}
