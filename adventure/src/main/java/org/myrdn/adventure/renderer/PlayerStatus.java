package org.myrdn.adventure.renderer;

import java.util.ArrayList;

public class PlayerStatus {

    public static volatile PlayerStatus playerStatus;

    private PlayerStatus() {

    }

    public static PlayerStatus createPlayerStatus() {
    
        if(playerStatus == null) {
    
            synchronized (PlayerStatus.class) {
    
                playerStatus = new PlayerStatus();
    
            }
    
        }
    
        return playerStatus;
    
    }

    public ArrayList<Object> update() {

        ArrayList<Object> renderObject = new ArrayList<>();

        return renderObject;

    }

}
