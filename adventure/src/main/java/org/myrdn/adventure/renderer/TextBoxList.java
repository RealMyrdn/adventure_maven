package org.myrdn.adventure.renderer;

import java.util.ArrayList;
import java.util.List;

public class TextBoxList {

    private static volatile TextBoxList textBoxList;

    private final ArrayList<TextBox> instances;

    private boolean canvasChanged;
    private int canvasWidth;
    private int canvasHeight;
    private char[][] canvas;
    private int canvasX;
    private int canvasY;

    private TextBoxList() {

        this.instances     = new ArrayList<>();
        this.canvasX       = 0;
        this.canvasY       = 0;
        this.canvasWidth   = 0;
        this.canvasHeight  = 0;
        this.canvasChanged = true;
        

    }

    public static TextBoxList createTextBoxList() {
    
        if(textBoxList == null) {
    
            synchronized (TextBoxList.class) {
    
                textBoxList = new TextBoxList();
    
            }
    
        }
    
        return textBoxList;
    
    }

    public int getSize() {

        return this.instances.size();

    }


    public void addTextBox(int boxPosX, int boxPosY, int width, int height, String text, String title) {

        TextBox textBox = new TextBox(boxPosX, boxPosY, width, height, text, title);
        instances.addLast(textBox);
        calculateCanvas();

        this.canvasChanged = true;

    }

    public void removeLast() {

        instances.removeLast();
        calculateCanvas();

        this.canvasChanged = true;

    }

    public void removeByTitle(String title) {

        for(TextBox textBox : instances) {

            if(textBox.getTitle().equals(title)) {

                instances.remove(textBox);

            }

        }
        
        calculateCanvas();

        this.canvasChanged = true;

    }

    public void clearList() {

        this.instances.clear();

    }

    public void calculateCanvas() {

        if(!instances.isEmpty()) {
            
            canvasX      = instances.stream().mapToInt(TextBox::getBoxPosX).min().orElse(0);
            canvasY      = instances.stream().mapToInt(TextBox::getBoxPosY).min().orElse(0);
            canvasWidth  = instances.stream().mapToInt(TextBox::getMaxX).max().orElse(0) - canvasX;
            canvasHeight = instances.stream().mapToInt(TextBox::getMaxY).max().orElse(0) - canvasY;
        
        } else {

            canvasX      = 0;
            canvasY      = 0;
            canvasWidth  = 0;
            canvasHeight = 0;

        }

    }
    
    public ArrayList<Object> update() {

        ArrayList<Object> renderObject = new ArrayList<>();
        char[][] newCanvas             = new char[canvasHeight][canvasWidth];

        renderObject.add(canvasX);
        renderObject.add(canvasY);

        if(canvasChanged) {

            for(TextBox instance : instances) {

                List<String> window = instance.getWindow();
                int startY          = instance.getBoxPosY() - canvasY;
                int startX          = instance.getBoxPosX() - canvasX;

                for(int row = 0; row < instance.getHeight(); row++) {
                
                    for(int col = 0; col < instance.getWidth(); col++) {
                    
                        newCanvas[startY + row][startX + col] = window.get(row).charAt(col);
                    
                    }
                
                }
            
            }

            this.canvas = newCanvas;
            this.canvasChanged = false;
        
        }

        renderObject.add(this.canvas);
        return renderObject;

    }

}
