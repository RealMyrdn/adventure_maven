package org.myrdn.adventure.renderer;

public final class RoomType {

    private static final char[] symbol = {'█','╻','╸','┓','╹','┃','┛','┫','╺','┏','━','┳','┗','┣','┻','╋'};

    public static char getSymbol(int index) {
        return symbol[index];
    }
}
