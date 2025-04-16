package org.myrdn.adventure.gamecontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.googlecode.lanterna.input.KeyStroke;

public class InputParser {

    public ArrayList<String> processKeyStrokes(ArrayList<KeyStroke> keyStrokes) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> instructions = new ArrayList<>();

        for(KeyStroke keyStroke : keyStrokes) {

            stringBuilder.append(keyStroke.getCharacter().toString());

        }

        String[] commands = stringBuilder.toString().toLowerCase().split(" ", 2);
        instructions.addAll(Arrays.asList(commands));

        return instructions;

    }

}
