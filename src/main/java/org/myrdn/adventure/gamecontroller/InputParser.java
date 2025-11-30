package org.myrdn.adventure.gamecontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class InputParser {

    public ArrayList<String> processString(String input) throws IOException {

        ArrayList<String> instructions = new ArrayList<>();

        if (input == null || input.isEmpty()) {
            return instructions;
        }

        String commandString = input.trim();

        if (commandString.isEmpty()) {
            return instructions;
        }

        String[] commands = commandString.toLowerCase().split(" ", 2);
        instructions.addAll(Arrays.asList(commands));

        return instructions;

    }

}
