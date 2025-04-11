package org.myrdn.adventure.renderer;

public final class RoomType {

    private static final char[] symbol = {'█','╻','╸','┓','╹','┃','┛','┫','╺','┏','━','┳','┗','┣','┻','╋'};

    public static String fromIndex(int index) {

        return String.valueOf(symbol[index]);
    
    }

}
