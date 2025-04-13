package org.myrdn.adventure.gamecontroller;

public class InputParser {

    private static volatile InputParser inputParser;

    private InputParser() {
        
    }

    public static InputParser createInputParser() {
    
        if(inputParser == null) {
    
            synchronized (InputParser.class) {
    
                inputParser = new InputParser();
    
            }
    
        }
    
        return inputParser;
    
    }

}
