package org.myrdn.adventure;

import java.io.Serializable;
import java.util.ArrayList;

public class GameObject implements Serializable {

    private static int counter = 0;
    private final int id;
    private final String name;
    private int hiddenStashes;
    private final String description;
    private final ArrayList<ItemObject> hiddenItems;

    public GameObject(String name, int hiddenStashes, String description) {
        this.id = counter++;
        this.name = name;
        this.hiddenStashes = hiddenStashes;
        this.description = description;
        this.hiddenItems = new ArrayList<>();
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

    public void setHiddenStashes(int hiddenStashes) {
        this.hiddenStashes = hiddenStashes;
    }

    public String getDescription() {
        return this.description;
    }

    public ArrayList<ItemObject> getHiddenItems() {
        return this.hiddenItems;
    }

    public void addtHiddenItem(ItemObject item) {
        this.hiddenItems.add(item);
    }

    public void removeHiddenItem(ItemObject item) {
        this.hiddenItems.remove(item);
    }
}
