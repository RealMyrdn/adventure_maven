package org.myrdn.adventure.renderer;

import java.util.ArrayList;
import java.util.Arrays;

public class TextBox {

    public static String HORIZONTAL_LINE = "═";
    public static String WHITE_SPACE = " ";

    private final ArrayList<String> window;
    private final String title;
    private final int boxPosX;
    private final int boxPosY;
    private final int height;
    private final int width;

    public TextBox(int boxPosX, int boxPosY, int width, int height, String text, String title) {

        this.boxPosX       = boxPosX;
        this.boxPosY       = boxPosY;
        this.height        = height;
        this.width         = width;
        this.title         = title;
        this.window        = addBorder(formatText(text));
    
    }

    public int getBoxPosX() {

        return this.boxPosX;

    }

    public int getBoxPosY() {

        return this.boxPosY;

    }

    public int getHeight() {

        return this.height;

    }

    public int getWidth() {

        return this.width;

    }

    public String getTitle() {

        return this.title;

    }

    protected int getMaxX() {

        return boxPosX + width;

    }

    protected int getMaxY() {

        return boxPosY + height;

    }

    protected ArrayList<String> getWindow() {

        return this.window;

    }

    private ArrayList<String> formatText(String text) {
        
        int charCounter = 0;
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> textList = new ArrayList<>(Arrays.asList(text.split(WHITE_SPACE)));
        ArrayList<String> formatText = new ArrayList<>();

        for(String word : textList) {
            
            if(charCounter + word.length() < this.width - 4 && !word.equals("\n")) {
            
                stringBuilder.append(word).append(WHITE_SPACE);
                charCounter += word.length() + 1;
            
            } else {

                formatText.add(stringBuilder.toString());
                stringBuilder.setLength(0);
                charCounter = 0;
                stringBuilder.append(word).append(WHITE_SPACE);
                charCounter += word.length() + 1;
            
            }

        }

        formatText.add(stringBuilder.toString());
        System.out.println(stringBuilder.toString());

        return formatText;

    }

    private ArrayList<String> addBorder(ArrayList<String> formattedText) {

        ArrayList<String> builtWindow = new ArrayList<>();

        int titleLength = title.length();
        int totalPadding = this.width - (6 + titleLength);
        int leftPadding = totalPadding / 2;
        int rightPadding = totalPadding - leftPadding;
    
        String topBorder = String.format("╔%s╡ %s ╞%s╗", HORIZONTAL_LINE.repeat(Math.max(0, leftPadding)), title, HORIZONTAL_LINE.repeat(Math.max(0, rightPadding)));
        builtWindow.add(topBorder);
    
        String emptyLine = String.format("║%s║", WHITE_SPACE.repeat(this.width - 2));
        builtWindow.add(emptyLine);
        
        if(formattedText != null && !formattedText.isEmpty()) {
            
            for (int i = 0; i < this.height - 3; i++) {
                
                if(i < formattedText.size()) {

                    if(this.width - formattedText.get(i).length() - 4 >= 0) {
                        
                        String textLine = String.format("║ %s%s ║", formattedText.get(i), WHITE_SPACE.repeat(this.width - formattedText.get(i).length() - 4));
                        builtWindow.add(textLine);
                    
                    } else {
                    
                        emptyLine = String.format("║%s║", WHITE_SPACE.repeat(this.width - 2));
                        builtWindow.add(emptyLine);
                    
                    }
                
                } else {
                    
                    emptyLine = String.format("║%s║", WHITE_SPACE.repeat(this.width - 2));
                    builtWindow.add(emptyLine);
                
                }
                
            }

        }
    
        String bottomBorder = String.format("╚%s╝", HORIZONTAL_LINE.repeat(this.width - 2));
        builtWindow.add(bottomBorder);
    
        return builtWindow;
    }

}