package org.myrdn.adventure;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private final String name;
    private int[] position;
    private int maxHealth;
    private int health;
    private int attack;
    private final ArrayList<Integer> inventory;
    private int equippedItem;

    public Player(String name, int[] position) {
        this.name = name;
        this.position = position;
        this.maxHealth = 10;
        this.health = 10;
        this.attack = 1;
        this.inventory = new ArrayList<>();
        this.equippedItem = 0;
    }

    public String getName() {
        return this.name;
    }

    public int[] getPosition() {
        return this.position;
    }

    public void setPosition(int[] position) {
        this.position = position;
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

    public void addItemInv(int item) {
        this.inventory.add(item);
    }

    public void removeItemInv(int item) {
        for(int i = 0; i < inventory.size(); i++) {
            if(inventory.get(i) == item) {
                inventory.remove(i);
            }
        }
    }

    public int getEquippedItem() {
        return this.equippedItem;
    }

    public void setEquippedItem(int item) {
        this.equippedItem = item;
    }

}
