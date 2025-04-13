package org.myrdn.adventure.renderer;

import java.util.ArrayList;

public class TextBoxList {

    private final ArrayList<TextBox> instances;

    private int canvasX;
    private int canvasY;
    private int canvasWidth;
    private int canvasHeight;
    private boolean canvasChanged;
    private char[][] canvas;

    public TextBoxList() {

        this.instances     = new ArrayList<>();
        this.canvasX       = 0;
        this.canvasY       = 0;
        this.canvasWidth   = 0;
        this.canvasHeight  = 0;
        this.canvasChanged = true;
        

    }


    public void addTextBox(int boxPosX, int boxPosY, int width, int height, String text, String title) {

        TextBox textBox = new TextBox(boxPosX, boxPosY, width, height, text, title);
        instances.add(textBox);
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

    public void calculateCanvas() {

        canvasX      = instances.stream().mapToInt(TextBox::getBoxPosX).min().orElseThrow();
        canvasY      = instances.stream().mapToInt(TextBox::getBoxPosY).min().orElseThrow();
        canvasWidth  = instances.stream().mapToInt(TextBox::getMaxX).max().orElse(0) - canvasX;
        canvasHeight = instances.stream().mapToInt(TextBox::getMaxY).max().orElse(0) - canvasY;

    }
    
    public ArrayList<Object> update() {

        ArrayList<Object> renderObject = new ArrayList<>();
        char[][] result = new char[canvasHeight][canvasWidth];

        renderObject.add(canvasX);
        renderObject.add(canvasY);

        if(canvasChanged) {
            for(TextBox instance : instances) {
                
                for(int i = instance.getBoxPosY(); i < instance.getHeight() + instance.getBoxPosY(); i++) {
                
                    for(int j = instance.getBoxPosX(); j < instance.getWidth() + instance.getBoxPosX(); j++) {
                    
                        result[i][j] = instance.getWindow().get(i).charAt(j);
                    
                    }
                
                }
                
            }
        
            this.canvas = result;
            this.canvasChanged = false;

        }

        renderObject.add(this.canvas);

        return renderObject;

    }

}
