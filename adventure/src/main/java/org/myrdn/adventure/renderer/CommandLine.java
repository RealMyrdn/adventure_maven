package org.myrdn.adventure.renderer;

import java.util.ArrayList;

import com.googlecode.lanterna.input.KeyStroke;

public class CommandLine {

    private static volatile CommandLine commandLine;

    private final ArrayList<KeyStroke> keystrokes;

    private CommandLine() {

        this.keystrokes = new ArrayList<>();

    }

    public static CommandLine createCommandLine() {
    
        if(commandLine == null) {
    
            synchronized (CommandLine.class) {
    
                commandLine = new CommandLine();
    
            }
    
        }
    
        return commandLine;
    
    }

    public ArrayList<KeyStroke> getKeyStrokes() {

        return this.keystrokes;

    }

    public void addKeyStroke(KeyStroke keyStroke) {

        this.keystrokes.add(keyStroke);
        
    }


    public ArrayList<Object> update() {

        ArrayList<Object> renderObject = new ArrayList<>();

        return renderObject;

    }

}
