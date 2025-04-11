package org.myrdn.adventure.renderer;

import org.myrdn.adventure.Player;
import org.myrdn.adventure.Room;

public class Map {

    private static volatile Map map;
    private final Renderer renderer;
    private final Player player;
    private final Room[][] rooms;
    private final int mapPosX;
    private final int mapPosY;
    private int playerPosX;
    private int playerPosY;
    private boolean mapFound;

    private Map(Renderer renderer, Player player, Room[][] rooms, int mapPosX, int mapPosY) {

        this.renderer   = renderer;
        this.player     = player;
        this.rooms      = rooms;
        this.mapPosX    = mapPosX;
        this.mapPosY    = mapPosY;
        this.playerPosX = player.getX();
        this.playerPosY = player.getY();
        this.mapFound   = false;
    
    }

    public void setMapFound() {
    
        this.mapFound = true;
    
    }

    public static Map createMap(Renderer renderer, Player player, Room[][] rooms, int mapPosX, int mapPosY) {
    
        if(map == null) {
    
            synchronized (Map.class) {
    
                map = new Map(renderer, player, rooms, mapPosX, mapPosY);
    
            }
    
        }
    
        return map;
    
    }

    public void update() {

        this.playerPosX = player.getX();
        this.playerPosY = player.getY();

        for(int y = 0; y < this.rooms.length; y++) {
    
            for(int x = 0; x < this.rooms[y].length; x++) {
    
                if(mapFound || (this.playerPosX == x && this.playerPosY == y)) {
    
                    renderer.getTextGraphics().putString(x + mapPosX, y + mapPosY, RoomType.fromIndex(rooms[y][x].getRoomType()));
                    
                } else {
    
                    renderer.getTextGraphics().putString(x + mapPosX, y + mapPosY, " ");
    
                }
    
            }
    
        }
    
    }

}
