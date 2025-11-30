package org.myrdn.adventure.gamecontroller;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void testProcessEmptyString() throws IOException {
        ArrayList<String> result = parser.processString("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessSingleWord() throws IOException {
        ArrayList<String> result = parser.processString("hilfe");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("hilfe", result.get(0));
    }

    @Test
    void testProcessMultipleWords() throws IOException {
        ArrayList<String> result = parser.processString("gehe nord");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("gehe", result.get(0));
        assertEquals("nord", result.get(1));
    }

    @Test
    void testProcessNullString() throws IOException {
        ArrayList<String> result = parser.processString(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessStringWithExtraSpaces() throws IOException {
        ArrayList<String> result = parser.processString("  gehe   nord  ");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("gehe", result.get(0));
    }

    @Test
    void testProcessUppercaseCommand() throws IOException {
        ArrayList<String> result = parser.processString("HILFE");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("hilfe", result.get(0));
    }
}
