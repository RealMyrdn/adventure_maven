package org.myrdn.adventure.gamecontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.googlecode.lanterna.input.KeyStroke;

public class InputParser {

    public ArrayList<String> processKeyStrokes(ArrayList<KeyStroke> keyStrokes) throws IOException {

        ArrayList<String> instructions = new ArrayList<>();

        if(keyStrokes == null || keyStrokes.isEmpty()) {
            return instructions;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for(KeyStroke keyStroke : keyStrokes) {

            stringBuilder.append(keyStroke.getCharacter().toString());

        }

        String commandString = stringBuilder.toString().trim();

        if(commandString.isEmpty()) {
            return instructions;
        }

        String[] commands = commandString.toLowerCase().split(" ", 2);
        instructions.addAll(Arrays.asList(commands));

        return instructions;

    }

}
