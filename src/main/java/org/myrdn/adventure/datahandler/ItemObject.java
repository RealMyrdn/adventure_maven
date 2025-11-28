package org.myrdn.adventure.datahandler;

import java.io.Serializable;

public class ItemObject implements Serializable {

    private static int counter = 0;
    private final int id;
    private final String name;
    private final int maxUses;
    private final String description;

    public ItemObject(String name, int maxUses, String description) {

        this.id          = counter++;
        this.name        = name;
        this.maxUses     = maxUses;
        this.description = description;
    
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

    public String getDescription() {
    
        return this.description;
    
    }

}
