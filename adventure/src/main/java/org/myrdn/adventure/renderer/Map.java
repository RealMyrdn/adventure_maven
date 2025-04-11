package org.myrdn.adventure.renderer;

import org.myrdn.adventure.Player;
import org.myrdn.adventure.Room;

public class Map {

    private static volatile Map map;
    private final Renderer renderer;
    private final Player player;
    private final Room[][] rooms;
    private final int mapX;
    private final int mapY;
    private int xPos;
    private int yPos;
    private boolean mapFound;

    private Map(Renderer renderer, Player player, Room[][] rooms, int mapX, int mapY) {

        this.renderer = renderer;
        this.player   = player;
        this.rooms    = rooms;
        this.mapX     = mapX;
        this.mapY     = mapY;
        this.xPos     = player.getX();
        this.yPos     = player.getY();
        this.mapFound = false;
    
    }

    public void setMapFound() {
    
        this.mapFound = true;
    
    }

    public static Map createMap(Renderer renderer, Player player, Room[][] rooms, int mapX, int mapY) {
    
        if(map == null) {
    
            synchronized (Map.class) {
    
                map = new Map(renderer, player, rooms, mapX, mapY);
    
            }
    
        }
    
        return map;
    
    }

    public void update() {

        this.xPos = player.getX();
        this.yPos = player.getY();

        for(int y = 0; y < this.rooms.length; y++) {
    
            for(int x = 0; x < this.rooms[y].length; x++) {
    
                if(mapFound || (this.xPos == x && this.yPos == y)) {
    
                    renderer.getTextGraphics().putString(x + mapX, y + mapY, RoomType.fromIndex(rooms[y][x].getRoomType()));
                    
                } else {
    
                    renderer.getTextGraphics().putString(x + mapX, y + mapY, " ");
    
                }
    
            }
    
        }
    
    }

}
