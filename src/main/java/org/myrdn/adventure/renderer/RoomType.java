package org.myrdn.adventure.renderer;

public final class RoomType {

    private static final char[] symbol = {'█','╻','╸','┓','╹','┃','┛','┫','╺','┏','━','┳','┗','┣','┻','╋'};

    public static char fromIndexAsChar(int index) {

        return symbol[index];
    
    }

    public static String fromIndexAsString(int index) {

        return String.valueOf(symbol[index]);
    }

}
