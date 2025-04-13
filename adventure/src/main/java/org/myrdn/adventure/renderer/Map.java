package org.myrdn.adventure.renderer;

import java.util.ArrayList;

import org.myrdn.adventure.gamecontroller.Player;
import org.myrdn.adventure.gamecontroller.Room;

public class Map {

    private static volatile Map map;

    private static final char HIDDEN = ' ';
    
    private final Player player;
    private final Room[][] rooms;
    private final int mapSizeX;
    private final int mapSizeY;
    private final int mapPosX;
    private final int mapPosY;

    private int playerPosX;
    private int playerPosY;
    private char[][] previousMap;
    private boolean mapFound;

    private Map(int mapSizeX, int mapSizeY, Player player, Room[][] rooms, int mapPosX, int mapPosY) {

        this.player      = player;
        this.rooms       = rooms;
        this.mapSizeX    = mapSizeX;
        this.mapSizeY    = mapSizeY;
        this.mapPosX     = mapPosX;
        this.mapPosY     = mapPosY;
        this.playerPosX  = player.getX();
        this.playerPosY  = player.getY();
        this.previousMap = new char[this.mapSizeY][mapSizeX];
        this.mapFound    = false;
    
    }

    public int getMapPosX() {

        return this.mapPosX;

    }

    public int getMapPoxY() {

        return this.mapPosY;

    }

    public void setMapFound() {
    
        this.mapFound = true;
    
    }

    public static Map createMap(int mapSizeX, int mapSizeY, Player player, Room[][] rooms, int mapPosX, int mapPosY) {
    
        if(map == null) {
    
            synchronized (Map.class) {
    
                map = new Map(mapSizeX, mapSizeY, player, rooms, mapPosX, mapPosY);
    
            }
    
        }
    
        return map;
    
    }

    public char[][] addBorder(char[][] map) {

        int originalHeight = map.length;
        int originalWidth = map[0].length;
        int newHeight = originalHeight + 2;
        int newWidth = originalWidth + 2;
    
        char[][] borderedMap = new char[newHeight][newWidth];
    
        borderedMap[0][0] = '╔';
        borderedMap[0][newWidth - 1] = '╗';
        borderedMap[newHeight - 1][0] = '╚';
        borderedMap[newHeight - 1][newWidth - 1] = '╝';

        String title = "╡ Karte ╞";

        int titleLength = title.length();
        int availableSpace = newWidth - 2;
        int titleStart = (availableSpace - titleLength) / 2;
    
        for(int x = 1; x < newWidth - 1; x++) {

            int titleIndex = x - titleStart;
            
            if(titleIndex >= 0 && titleIndex < titleLength) {
            
                borderedMap[0][x] = title.charAt(titleIndex);
            
            } else {
            
                borderedMap[0][x] = '═';
            
            }
        
        }

        for(int x = 1; x < newWidth - 1; x++) {
        
            borderedMap[newHeight - 1][x] = '═';
        
        }
    
        for(int y = 1; y < newHeight - 1; y++) {
        
            borderedMap[y][0] = '║';
            borderedMap[y][newWidth - 1] = '║';
        
        }
    
        for(int y = 0; y < originalHeight; y++) {
        
            System.arraycopy(map[y], 0, borderedMap[y + 1], 1, originalWidth);
        
        }
    
        return borderedMap;
    }

    public ArrayList<Object> update() {

        ArrayList<Object> renderObject = new ArrayList<>();

        this.playerPosX = player.getX();
        this.playerPosY = player.getY();

        if(player.hasMoved()) {
        
            char[][] newMap = new char[mapSizeY][mapSizeX];

            for(int y = 0; y < this.rooms.length; y++) {
        
                for(int x = 0; x < this.rooms[y].length; x++) {
        
                    newMap[y][x] = isTileVisible(x, y) ? RoomType.fromIndexAsChar(rooms[y][x].getRoomType()) : HIDDEN;

                }

            }
        
            this.previousMap = newMap;

        }

        renderObject.add(mapPosX);
        renderObject.add(mapPosY);
        renderObject.add(addBorder(this.previousMap));

        return renderObject;

    }

    private boolean isTileVisible(int x, int y) {
    
        return mapFound || (playerPosX == x && playerPosY == y);
    
    }

}
