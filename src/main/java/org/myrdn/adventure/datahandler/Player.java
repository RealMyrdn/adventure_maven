package org.myrdn.adventure.datahandler;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents the player character in the game.
 * Manages player position, health, attack, and inventory.
 */
public class Player implements Serializable {
    
    private final ArrayList<ItemObject> inventory;
    private final String name;

    private int previousPosX;
    private int previousPosY;
    private int equippedItem;
    private int playerPosX;
    private int playerPosY;
    private int maxHealth;
    private int health;
    private int attack;

    public Player(String name, int PosX, int PosY) {
    
        this.name             = name;
        this.playerPosX       = PosX;
        this.playerPosY       = PosY;
        this.previousPosX     = 0;
        this.previousPosY     = 0;
        this.maxHealth        = 15;
        this.health           = 15;
        this.attack           = 1;
        this.inventory        = new ArrayList<>();
        this.equippedItem     = 0;
    
    }

    public String getName() {
    
        return this.name;
    
    }

    public int getX() {
    
        return this.playerPosX;
    
    }

    public int getY() {
    
        return this.playerPosY;
    
    } 

    /**
     * Sets the player's position and tracks the previous position.
     *
     * @param posX New X coordinate
     * @param posY New Y coordinate
     */
    public void setPosition(int posX, int posY) {

        this.previousPosX = this.playerPosX;
        this.previousPosY = this.playerPosY;
        this.playerPosX   = posX;
        this.playerPosY   = posY;

    }

    public int getMaxHealth() {
    
        return this.maxHealth;
    
    }

    public void setMaxHealth(int maxHealth) {
    
        this.maxHealth = maxHealth;
    
    }

    public int getHealth() {
    
        return this.health;
    
    }

    /**
     * Sets the player's health, capping it at max health.
     *
     * @param health The new health value
     */
    public void setHealth(int health) {

        if(health > this.maxHealth) {

            this.health = this.maxHealth;

        } else {

            this.health = health;

        }

    }

    public int getAttack() {
    
        return this.attack;
    
    }

    public void setAttack(int attack) {
    
        this.attack = attack;
    
    }

    /**
     * Adds an item to the player's inventory.
     *
     * @param item The item to add
     */
    public void addItemInv(ItemObject item) {

        this.inventory.add(item);

    }

    /**
     * Removes an item from the player's inventory.
     *
     * @param item The item to remove
     */
    public void removeItemInv(ItemObject item) {

        inventory.remove(item);

    }

    public ArrayList<ItemObject> getIventory() {

        return this.inventory;

    }

    public String getIventoryAsList() {

        StringBuilder stringBuilder = new StringBuilder();

        for(ItemObject item : this.inventory) {

            stringBuilder.append(item.getName()).append("\n");

        }

        return stringBuilder.toString();

    }

    /**
     * Gets an item from the inventory by name (case-insensitive).
     *
     * @param itemName The name of the item to find
     * @return The ItemObject if found, null otherwise
     */
    public ItemObject getItemFromInventory(String itemName) {

        if(itemName == null) return null;

        for(ItemObject item : this.inventory) {

            if(item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }

        }

        return null;

    }

    public int getEquippedItem() {

        return this.equippedItem;
    
    }

    public void setEquippedItem(int item) {
        
        this.equippedItem = item;
    
    }

    /**
     * Checks if the player has moved since the last position update.
     *
     * @return true if player has moved, false otherwise
     */
    public boolean hasMoved() {

        return (this.previousPosX != this.playerPosX) || (this.previousPosY != this.playerPosY);

    }

}
