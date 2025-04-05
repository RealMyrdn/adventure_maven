package org.myrdn.adventure;

import java.io.Serializable;

public class GameObject implements Serializable {

    private static int counter = 0;
    private final int id;
    private final String name;
    private final int hiddenStashes;
    private int placements;
    private final String description;

    public GameObject(String name, int hiddenStashes, String description) {
        this.id = counter++;
        this.name = name;
        this.hiddenStashes = hiddenStashes;
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getHiddenStashes() {
        return this.hiddenStashes;
    }

    public int getPlacements() {
        return this.placements;
    }

    public void setPlacements(int placements) {
        this.placements = placements;
    }

    public String getDescription() {
        return this.description;
    }
}
