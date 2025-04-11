package org.myrdn.adventure.renderer;

import java.util.ArrayList;
import java.util.Arrays;

public class TextBox {

    private final int mapPosX;
    private final int mapPosY;
    private final int height;
    private final int width;
    private final String text;
    private final ArrayList<String> formattedText;

    public TextBox(int mapPosX, int mapPosY, int width, int height, String text) {

        this.mapPosX          = mapPosX;
        this.mapPosY          = mapPosY;
        this.height        = height;
        this.width         = width;
        this.text          = text;
        this.formattedText = formatText();
    
    }

    public int getmapPosX() {

        return this.mapPosX;

    }

    public int getmapPosY() {

        return this.mapPosY;

    }

    public int getHeight() {

        return this.height;

    }

    public int getWidth() {

        return this.width;

    }

    private ArrayList<String> formatText() {
        
        int charCounter = 0;
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> textList = new ArrayList<>(Arrays.asList(this.text.split(" ")));
        ArrayList<String> formatText = new ArrayList<>();

        for(String word : textList) {
            
            if(charCounter + word.length() < width && !word.equals("\n")) {
            
                stringBuilder.append(word).append(" ");
                charCounter += word.length() + 1;
            
            } else {

                formattedText.add(stringBuilder.toString());
                stringBuilder.setLength(0);
                charCounter = 0;
                charCounter += word.length();
                stringBuilder.append(word).append(" ");
            
            }

        }

        return formatText;

    }

}
