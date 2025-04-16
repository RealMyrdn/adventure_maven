package org.myrdn.adventure.renderer;

import java.util.ArrayList;

import org.myrdn.adventure.datahandler.Player;

public class PlayerStatus {

    public static volatile PlayerStatus playerStatus;
    private final Player player;

    private PlayerStatus(Player player) {

        this.player = player;

    }

    public static PlayerStatus createPlayerStatus(Player player) {
    
        if(playerStatus == null) {
    
            synchronized (PlayerStatus.class) {
    
                playerStatus = new PlayerStatus(player);
    
            }
    
        }
    
        return playerStatus;
    
    }

    public ArrayList<Object> update() {

        ArrayList<Object> renderObject = new ArrayList<>();
        ArrayList<String> builtWindow = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();
        String title                = player.getName();
        int width                   = player.getMaxHealth() + 4;
        int titleLength             = title.length();
        int totalPadding            = width - (6 + titleLength);
        int leftPadding             = totalPadding / 2;
        int rightPadding            = totalPadding - leftPadding;
        String health;

        for(int i = player.getMaxHealth(); i > 0; i--) {

            if(player.getHealth() >= i) {

                stringBuilder.append("▓");

            } else {

                stringBuilder.append("░");
            }

        }

        health = stringBuilder.toString();
    
        String topBorder = String.format("╔%s╡ %s ╞%s╗", "═".repeat(Math.max(0, leftPadding)), title, "═".repeat(Math.max(0, rightPadding)));
        builtWindow.add(topBorder);
                        
        String textLine = String.format("║ %s ║", health);
        builtWindow.add(textLine);
                    
        String bottomBorder = String.format("╚%s╝", "═".repeat(width - 2));
        builtWindow.add(bottomBorder);

        char[][] status = new char[3][width];

        int i = 0;

        for(String line : builtWindow) {
            
            for(int j = 0; j < line.length(); j++) {

                status[i][j] = line.charAt(j);

            }

            i++;
        
        }

        renderObject.add(119 - width);
        renderObject.add(1);
        renderObject.add(status);

        return renderObject;

    }

}
