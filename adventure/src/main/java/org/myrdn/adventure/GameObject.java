package org.myrdn.adventure;

import java.io.Serializable;

public class GameObject implements Serializable {

    private final int id;
    private final String name;
    private final int hiddenStashes;
    private int placements;
    private final String description;

    public GameObject(int id, String name, int hiddenStashes, int placements, String description) {
        this.id = id;
        this.name = name;
        this.hiddenStashes = hiddenStashes;
        this.placements = placements;
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
