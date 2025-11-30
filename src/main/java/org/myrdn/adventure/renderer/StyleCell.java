package org.myrdn.adventure.renderer;

import com.badlogic.gdx.graphics.Color;

import java.util.HashSet;
import java.util.Set;

public class StyleCell {

    public enum Style {
        BOLD,
        ITALIC,
        UNDERLINE
    }

    public Color color;
    public Set<Style> styles;

    public StyleCell(Color color, Set<Style> styles) {

        this.color = color;
        this.styles = styles;

    }

    public static StyleCell defaultStyle() {

        return new StyleCell(Color.WHITE, new HashSet<>());

    }
}
