package org.myrdn.adventure.renderer;

public enum RoomType {

    EMPTY(0, '█'),
    DOOR_DOWN(1, '╻'),
    DOOR_LEFT(2, '╸'),
    CORNER_DL(3, '┓'),
    DOOR_UP(4, '╹'),
    DOORS_VERTICAL(5, '┃'),
    CORNER_LU(6, '┛'),
    T_LEFT(7, '┫'),
    DOOR_RIGHT(8, '╺'),
    CORNER_RD(9, '┏'),
    DOORS_HORIZONTAL(10, '━'),
    T_DOWN(11, '┳'),
    CORNER_RU(12, '┗'),
    T_RIGHT(13, '┣'),
    T_UP(14, '┻'),
    CROSS(15, '╋');

    private final int type;
    private final char symbol;

    RoomType(int type, char symbol) {
        this.type = type;
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    public int getType() {
        return type;
    }
}
