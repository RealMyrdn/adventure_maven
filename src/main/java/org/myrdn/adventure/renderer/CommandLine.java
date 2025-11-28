package org.myrdn.adventure.renderer;

import java.util.ArrayList;

import com.googlecode.lanterna.input.KeyStroke;

public class CommandLine {

    private static volatile CommandLine commandLine;

    private final ArrayList<Character> keyStrokes;
    private final int posX;
    private final int posY;

    private CommandLine() {

        this.keyStrokes = new ArrayList<>();
        this.posX       = 2;
        this.posY       = 37;

    }

    public static CommandLine createCommandLine() {
    
        if(commandLine == null) {
    
            synchronized (CommandLine.class) {
    
                commandLine = new CommandLine();
    
            }
    
        }
    
        return commandLine;
    
    }

    public void addKeyStroke(KeyStroke keyStroke) {

        Character newChar = keyStroke.getCharacter();

        if (newChar != null && !Character.isISOControl(newChar)) {

            this.keyStrokes.add(newChar);

        }
        
    }

    public void removeLast() {
        
        if(keyStrokes.size() >= 1) {
            
            this.keyStrokes.removeLast();
        
        } else {

            this.keyStrokes.clear();

        }

    }

    public void resetKeyStrokes() {

        this.keyStrokes.clear();

    }


    public ArrayList<Object> update() {

        ArrayList<Object> renderObject = new ArrayList<>();

        char[][] characters = new char[1][80];

        if(!keyStrokes.isEmpty()) {

            for(int i = 0; i < keyStrokes.size(); i++) {
                
                characters[0][i] = keyStrokes.get(i);
            
            }

        } else {

            characters[0][0] = ' ';
        
        }

        renderObject.add(this.posX);
        renderObject.add(this.posY);
        renderObject.add(characters);

        return renderObject;

    }

}
