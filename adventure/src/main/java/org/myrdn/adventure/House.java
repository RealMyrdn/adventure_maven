package org.myrdn.adventure;

import java.io.Serializable;

public class House implements Serializable {

    private final int[][] layout;
    private final int[] startPosition;

    public House(int[] startPosition, int[][] layout) {
        this.layout = layout;
        this.startPosition = startPosition;
    }

    public int[] getStartPosition() {
        return this.startPosition;
    }

    public int[][] getLayout() {
        return this.layout;
    }

    public String getMap() {
        StringBuilder stringbuilder = new StringBuilder();
        char[] symbol = {'█','╻','╸','┓','╹','┃','┛','┫','╺','┏','━','┳','┗','┣','┻','╋'};
        for(int[] row : this.getLayout()) {
            for(int item : row) {
                stringbuilder.append(symbol[item]);
            }
            stringbuilder.append("\n");
        }
        return stringbuilder.toString();
    }

}
