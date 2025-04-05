package org.myrdn.adventure;

import java.io.Serializable;

public class GameObject implements Serializable {

    private final String name;

    public GameObject(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
