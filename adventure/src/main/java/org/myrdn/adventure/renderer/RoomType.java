package org.myrdn.adventure.renderer;

public final class RoomType {

    private final char[] symbol = {'█','╻','╸','┓','╹','┃','┛','┫','╺','┏','━','┳','┗','┣','┻','╋'};

    public char getSymbol(int index) {
        return symbol[index];
    }
}
