package org.myrdn.adventure.datahandler;

import java.io.Serializable;
import java.util.ArrayList;

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
        this.health           = 10;
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

    public void addItemInv(ItemObject item) {
    
        this.inventory.add(item);
    
    }

    public void removeItemInv(ItemObject item) {

        inventory.remove(item);
    
    }

    public ArrayList<ItemObject> getIventory() {

        return this.inventory;

    }

    public String getIventoryAsList() {

        StringBuilder stringBuilder = new StringBuilder();

        for(ItemObject item : this.inventory) {

            stringBuilder.append(item.getName()).append(" ");

        }
        
        return stringBuilder.toString();
        
    }

    public int getEquippedItem() {

        return this.equippedItem;
    
    }

    public void setEquippedItem(int item) {
        
        this.equippedItem = item;
    
    }

    public boolean hasMoved() {

        return (this.previousPosX != this.playerPosX) || (this.previousPosY != this.playerPosY);

    }

}
