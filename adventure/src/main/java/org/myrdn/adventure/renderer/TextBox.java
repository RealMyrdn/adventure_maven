package org.myrdn.adventure.renderer;

import java.util.ArrayList;
import java.util.Arrays;

public class TextBox {

    private final int xPos;
    private final int yPos;
    private final int height;
    private final int width;
    private final String text;
    private final ArrayList<String> formattedText;

    public TextBox(int xPos, int yPos, int width, int height, String text) {

        this.xPos          = xPos;
        this.yPos          = yPos;
        this.height        = height;
        this.width         = width;
        this.text          = text;
        this.formattedText = formatText();
    
    }

    public int getXPos() {

        return this.xPos;

    }

    public int getYPos() {

        return this.yPos;

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
