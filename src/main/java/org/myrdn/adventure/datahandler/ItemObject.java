package org.myrdn.adventure.datahandler;

import java.io.Serializable;

public class ItemObject implements Serializable {

    public enum EffectType {
        NONE,           // Kein Effekt
        HEAL,           // Heilt HP
        ATTACK_BOOST,   // Erhöht Angriff
        DEFENSE_BOOST,  // Erhöht Verteidigung
        DAMAGE,         // Verursacht Schaden an Gegnern
        FLEE            // Garantiert Flucht
    }

    private static int counter = 0;
    private final int id;
    private final String name;
    private final int maxUses;
    private final String description;
    private final EffectType effectType;
    private final int effectValue;

    private int currentUses;

    public ItemObject(String name, int maxUses, String description) {
        this(name, maxUses, description, EffectType.NONE, 0);
    }

    public ItemObject(String name, int maxUses, String description, EffectType effectType, int effectValue) {
        this.id          = counter++;
        this.name        = name;
        this.maxUses     = maxUses;
        this.currentUses = maxUses;
        this.description = description;
        this.effectType  = effectType;
        this.effectValue = effectValue;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public int getCurrentUses() {
        return this.currentUses;
    }

    public boolean use() {
        if (maxUses == -1) {
            // Unbegrenzte Nutzung (permanente Items)
            return true;
        }
        if (currentUses > 0) {
            currentUses--;
            return true;
        }
        return false;
    }

    public boolean isUsedUp() {
        return maxUses != -1 && currentUses <= 0;
    }

    public String getDescription() {
        return this.description;
    }

    public EffectType getEffectType() {
        return this.effectType;
    }

    public int getEffectValue() {
        return this.effectValue;
    }

    public boolean isConsumable() {
        return maxUses > 0;
    }

    public boolean isPermanent() {
        return maxUses == -1;
    }
}
