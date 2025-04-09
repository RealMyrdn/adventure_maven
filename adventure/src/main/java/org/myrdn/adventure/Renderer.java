package org.myrdn.adventure;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
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
        this.mapFound = true;
    }

    public Terminal getTerminal() {
        return this.terminal;
    }

    public Screen getScreen() {
        return this.screen;
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
        DefaultTerminalFactory factory = new DefaultTerminalFactory()
            .setTerminalEmulatorFontConfiguration(myFontConfiguration)
            .setInitialTerminalSize(size);

        this.terminal = factory.createTerminal();

        if (terminal instanceof SwingTerminalFrame swingTerminal) {
            swingTerminal.setResizable(false);
            swingTerminal.setTitle("🐱‍👤 Textadventure");
            swingTerminal.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("Fenster wird geschlossen, Spiel wird beendet...");
                    try {
                        screen.stopScreen();
                    } catch (IOException ioException) {
                        System.out.println(ioException);
                    }
                    System.exit(0);
                }
            });
        }

        this.screen = new TerminalScreen(this.terminal);
        this.screen.startScreen();
        this.textGraphics = screen.newTextGraphics();
    }

    public void renderFrame() throws IOException {
        screen.refresh();
    }

    public void printMap(String map, int playerX, int playerY) {
        String[] rows = map.split("\n");
        for (int i = 0; i < this.xSize + 2; i++) {
            putGreenBorder(i, 0);
        }
        for (int y = 0; y < rows.length; y++) {
            putGreenBorder(0, y + 1);
            String[] cells = rows[y].split("");
            for (int x = 0; x < cells.length; x++) {
                putMazeTiles(x, y, cells, playerX, playerY);
            }
            putGreenBorder(xSize + 1, y + 1);
        }
        for (int i = 0; i < this.xSize + 2; i++) {
            putGreenBorder(i, ySize + 1);
        }
        putMapTitle();
    }

    public void putMapTitle() {
        this.textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        this.textGraphics.putString((this.xSize - 3) / 2, this.ySize + 1, "Karte", SGR.BOLD);
    }

    public void putGreenBorder(int x, int y) {
        this.textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        this.textGraphics.setBackgroundColor(TextColor.ANSI.YELLOW);
        this.textGraphics.putString(x, y, " ");
    }

    public void putMazeTiles(int x, int y, String[] cells, int playerX, int playerY) {
        this.textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
        this.textGraphics.setForegroundColor(TextColor.ANSI.BLUE_BRIGHT);
        if (mapFound || (x == playerX && y == playerY)) {
            this.textGraphics.putString(x + 1, y + 1, cells[x]);
        }
    }
    public void clearTextField(String backgroundColor, String foregroundColor, int height) {
        this.textGraphics.setBackgroundColor(TextColor.ANSI.valueOf(backgroundColor));
        this.textGraphics.setForegroundColor(TextColor.ANSI.valueOf(foregroundColor));
        this.textGraphics.fillRectangle(new TerminalPosition(40, height), new TerminalSize(80, 8), ' ');
    }

    public void printRoomDescription(String description, int height) {
        clearTextField("BLACK", "CYAN_BRIGHT", height);
        String[] rows = description.split("\n");
        for (int y = 0; y < rows.length; y++) {
            this.textGraphics.putString(40, height + y, rows[y]);
        }
    }

    public void printObjectDescription(String description, ArrayList<ItemObject> itemObjects, int height) {
        clearTextField("BLACK", "GREEN_BRIGHT", height);
        
        StringBuilder stringBuilder = new StringBuilder();
        String[] splitDescription = description.split(" ");
        
        int charCounter = 0;

        for(String word : splitDescription) {
            if(charCounter + word.length() < 60) {
                stringBuilder.append(word).append(" ");
                charCounter += word.length() + 1;
            } else {
                charCounter = 0;
                charCounter += word.length();
                stringBuilder.append("\n").append(word).append(" ");
            }
        }
        stringBuilder.append("\n");
        if(!itemObjects.isEmpty()) {
            stringBuilder.append("Das Objekte enthält folgende Gegenstände: \n");
            for(ItemObject item : itemObjects) {
                stringBuilder.append(item.getName()).append("\n");
            }
        }
        String objectDescription = stringBuilder.toString();
        String[] rows = objectDescription.split("\n");
        for (int y = 0; y < rows.length; y++) {
            this.textGraphics.putString(40, height + y, rows[y]);
        }
    }

    public void printDescription(String description, int height) {
        clearTextField("BLACK", "CYAN_BRIGHT", height);
        String[] rows = description.split("\n");
        for (int y = 0; y < rows.length; y++) {
            this.textGraphics.putString(40, height + y, rows[y]);
        }
    }

    public void printInputLine(ArrayList<KeyStroke> keyStrokes) {
        this.textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
        this.textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        for (int i = 0; i < 40; i++) {
            this.textGraphics.putString(10 + i, 35, " ");
        }
        int j = 0;
        for (KeyStroke keyStroke : keyStrokes) {
            this.textGraphics.putString(10 + j, 35, keyStroke.getCharacter().toString());
            j++;
        }
    }
}