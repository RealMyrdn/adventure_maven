package org.myrdn.adventure;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

public class Renderer {

    private final Font myFont;
    private final SwingTerminalFontConfiguration myFontConfiguration;
    private Terminal terminal;
    private Screen screen;
    private TextGraphics textGraphics;
    private final TerminalSize size;
    private final int xSize;
    private final int ySize;
    private boolean mapFound;

    public Renderer(int xSize, int ySize) throws IOException {
        this.size = new TerminalSize(120, 40);
        this.myFont = loadFontFromResources("/fonts/JetBrainsMono-Regular.ttf", 14f);
        this.myFontConfiguration = SwingTerminalFontConfiguration.newInstance(myFont);
        this.xSize = xSize;
        this.ySize = ySize;
        this.mapFound = false;
    }

    public void setMapFound(boolean mapFound) {
        this.mapFound = mapFound;
    }

        private Font loadFontFromResources(String path, float size) {
        try (InputStream fontStream = getClass().getResourceAsStream(path)) {
            if (fontStream == null) {
                throw new IOException("Font-Datei nicht gefunden: " + path);
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(size);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        } catch (Exception e) {
            System.err.println("Fehler beim Laden des Fonts, es wird der Standardfont verwendet.");
            return new Font("Monospaced", Font.PLAIN, (int) size);
        }
    }

    public void initScreen() throws IOException {
        DefaultTerminalFactory factory = new DefaultTerminalFactory().setTerminalEmulatorFontConfiguration(myFontConfiguration).setInitialTerminalSize(size);
        this.terminal = factory.createTerminal();
        if(terminal instanceof SwingTerminalFrame swingTerminal) {
            swingTerminal.setResizable(false);
            swingTerminal.setTitle("🐱‍👤 Textadventure");
        }
        this.screen = new TerminalScreen(this.terminal);
        this.screen.startScreen();
        this.textGraphics = screen.newTextGraphics();
    }

    public void printMap(String map, int playerX, int playerY) throws IOException {
        String[] rows = map.split("\n");
        for(int i = 0; i < this.xSize + 2; i++) {
            putGreenBorder(i, 0);
        }
        for(int y = 0; y < rows.length; y++) {
            putGreenBorder(0, y + 1);
            String[] cells = rows[y].split("");
            for(int x = 0; x < cells.length; x++) {
                putMazeTiles(x, y, cells, playerX, playerY);
            }
            putGreenBorder(xSize + 1, y + 1);
        }
        for(int i = 0; i < this.xSize + 2; i++) {
            putGreenBorder(i, ySize + 1);
        }
        putMapTitle();
        screen.refresh();
    }

    public void putMapTitle() {
        this.textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        this.textGraphics.putString((this.xSize - 3) / 2, this.ySize + 1, "Karte", SGR.BOLD);
    }

    public void putGreenBorder(int x, int y) {
        this.textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        this.textGraphics.setBackgroundColor(TextColor.ANSI.YELLOW);
        this.textGraphics.putString(x, y," ");
    }

    public void putMazeTiles(int x, int y, String[] cells, int playerX, int playerY) {
        this.textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
        this.textGraphics.setForegroundColor(TextColor.ANSI.BLUE_BRIGHT);
        if(mapFound || (x == playerX && y == playerY)) {
            this.textGraphics.putString(x + 1, y + 1, cells[x]);
        }
    }

    public void printRoomDescription(String description) throws IOException {
        this.textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
        this.textGraphics.setForegroundColor(TextColor.ANSI.CYAN_BRIGHT);
        String[] rows = description.split("\n");
        for(int y = 0; y < rows.length; y++) {
            this.textGraphics.putString(40, 30 + y, rows[y]);
        }
        screen.refresh();
    }
}
