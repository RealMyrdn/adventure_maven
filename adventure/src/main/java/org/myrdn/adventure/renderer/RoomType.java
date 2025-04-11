package org.myrdn.adventure.renderer;

public final class RoomType {

    private static final String[] symbol = {"█","╻","╸","┓","╹","┃","┛","┫","╺","┏","━","┳","┗","┣","┻","╋"};

    public static String fromIndex(int index) {

        return symbol[index];
    
    }

}
