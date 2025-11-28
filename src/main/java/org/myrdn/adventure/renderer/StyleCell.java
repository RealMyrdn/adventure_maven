package org.myrdn.adventure.renderer;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;

public class StyleCell {

    public TextColor color;
    public Set<SGR> styles;

    public StyleCell(TextColor color, Set<SGR> styles) {

        this.color = color;
        this.styles = styles;
        
    }

    public static StyleCell defaultStyle() {

        return new StyleCell(TextColor.ANSI.DEFAULT, new HashSet<>());
    
    }
}