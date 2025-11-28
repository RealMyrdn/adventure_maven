package org.myrdn.adventure.datahandler;

import java.io.Serializable;

public class SaveGame implements Serializable {

    private final Player player;
    private final Dungeon dungeon;
    
    public SaveGame(Player player, Dungeon dungeon) {
    
        this.player = player;
        this.dungeon = dungeon;
    
    }

    public String getPlayerName() {
    
        return this.player.getName();
    
    }

    public Dungeon getHouse() {
    
        return this.dungeon;
    
    }
    
}
