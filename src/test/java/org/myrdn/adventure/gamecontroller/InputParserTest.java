package org.myrdn.adventure.gamecontroller;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InputParser class.
 */
class InputParserTest {

    private InputParser parser;

    @BeforeEach
    void setUp() {
        parser = new InputParser();
    }

    @Test
    void testProcessEmptyKeyStrokes() throws IOException {
        ArrayList<KeyStroke> keyStrokes = new ArrayList<>();
        ArrayList<String> result = parser.processKeyStrokes(keyStrokes);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessSingleWord() throws IOException {
        ArrayList<KeyStroke> keyStrokes = new ArrayList<>();
        keyStrokes.add(new KeyStroke('h', false, false));
        keyStrokes.add(new KeyStroke('i', false, false));
        keyStrokes.add(new KeyStroke('l', false, false));
        keyStrokes.add(new KeyStroke('f', false, false));
        keyStrokes.add(new KeyStroke('e', false, false));

        ArrayList<String> result = parser.processKeyStrokes(keyStrokes);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("hilfe", result.get(0));
    }

    @Test
    void testProcessMultipleWords() throws IOException {
        ArrayList<KeyStroke> keyStrokes = new ArrayList<>();

        // "gehe nord"
        keyStrokes.add(new KeyStroke('g', false, false));
        keyStrokes.add(new KeyStroke('e', false, false));
        keyStrokes.add(new KeyStroke('h', false, false));
        keyStrokes.add(new KeyStroke('e', false, false));
        keyStrokes.add(new KeyStroke(' ', false, false));
        keyStrokes.add(new KeyStroke('n', false, false));
        keyStrokes.add(new KeyStroke('o', false, false));
        keyStrokes.add(new KeyStroke('r', false, false));
        keyStrokes.add(new KeyStroke('d', false, false));

        ArrayList<String> result = parser.processKeyStrokes(keyStrokes);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("gehe", result.get(0));
        assertEquals("nord", result.get(1));
    }

    @Test
    void testProcessNullKeyStrokes() throws IOException {
        ArrayList<String> result = parser.processKeyStrokes(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
