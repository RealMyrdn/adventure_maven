package org.myrdn.adventure.datahandler;

import java.io.Serializable;

public class Enemy implements Serializable {

    private final String name;
    private final String description;
    private final int maxHealth;
    private final int attack;
    private final int defense;

    private int health;
    private boolean alive;

    public Enemy(String name, int health, int attack, int defense, String description) {
        this.name = name;
        this.maxHealth = health;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.description = description;
        this.alive = true;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getMaxHealth() {
        return this.maxHealth;
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, health);
        if (this.health <= 0) {
            this.alive = false;
        }
    }

    public void takeDamage(int damage) {
        int actualDamage = Math.max(1, damage - this.defense);
        setHealth(this.health - actualDamage);
    }

    public int getAttack() {
        return this.attack;
    }

    public int getDefense() {
        return this.defense;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public Enemy copy() {
        return new Enemy(this.name, this.maxHealth, this.attack, this.defense, this.description);
    }
}
