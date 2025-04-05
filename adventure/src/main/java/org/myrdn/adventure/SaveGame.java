package org.myrdn.adventure;

public class SaveGame {
    private final Player player;
    private final House house;
    public SaveGame(Player player, House house) {
        this.player = player;
        this.house = house;
    }

    public String getPlayerName() {
        return this.player.getName();
    }
}
