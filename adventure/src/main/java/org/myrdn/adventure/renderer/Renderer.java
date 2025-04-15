package org.myrdn.adventure.renderer;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.myrdn.adventure.datahandler.ItemObject;
import org.myrdn.adventure.datahandler.Player;
import org.myrdn.adventure.gamecontroller.Generator;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import static com.googlecode.lanterna.TerminalTextUtils.isControlCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

public class Renderer {

    private final SwingTerminalFontConfiguration myFontConfiguration;
    private final PlayerStatus playerStatus;
    private final TextBoxList textBoxList;
    private final CommandLine commandLine;
    private final Generator generator;
    private final int terminalX = 120;
    private final int terminalY = 40;
    private final TerminalSize size;
    private final Player player;
    private final Font myFont;
    private final Map map;
   
    protected final int xSize;
    protected final int ySize;
   
    private TextGraphics textGraphics;
    private Terminal terminal;
    private Screen screen;

    public Renderer(Generator generator, Player player) throws IOException {

        this.generator           = generator;
        this.size                = new TerminalSize(terminalX, terminalY);
        this.myFont              = loadFontFromResources("/fonts/JetBrainsMono-Regular.ttf", 16f);
        this.myFontConfiguration = SwingTerminalFontConfiguration.newInstance(myFont);
        this.xSize               = this.generator.getXSize();
        this.ySize               = this.generator.getYSize();
        this.map                 = Map.createMap(xSize, ySize, player, generator.getRooms(), 1, 1);
        this.textBoxList         = TextBoxList.createTextBoxList();
        this.commandLine         = CommandLine.createCommandLine();
        this.player              = player;
        this.playerStatus        = PlayerStatus.createPlayerStatus(this.player);

    }

    public Terminal getTerminal() {
        
        return this.terminal;
    
    }

    public Screen getScreen() {
    
        return this.screen;
    
    }

    public TextGraphics getTextGraphics() {
    
        return this.textGraphics;
    
    }

    public TextBoxList getTextBoxList() {

        return this.textBoxList;
        
    }

    private Font loadFontFromResources(String path, float size) {
    
        try (InputStream fontStream = getClass().getResourceAsStream(path)) {
    
            if (fontStream == null) {
    
                throw new IOException("Font-Datei nicht gefunden: " + path);
    
            }
    
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.BOLD, size);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
    
            return font;
    
        } catch (Exception e) {
    
            System.err.println("Fehler beim Laden des Fonts, es wird der Standardfont verwendet.");
            return new Font("Monospaced", Font.PLAIN, (int) size);
    
        }
    
    }

    public void initScreen() throws IOException {

        System.setProperty("file.encoding", "UTF-8");
    
        DefaultTerminalFactory factory = new DefaultTerminalFactory().setTerminalEmulatorFontConfiguration(myFontConfiguration).setInitialTerminalSize(size);

        this.terminal = factory.createTerminal();

        if(terminal instanceof SwingTerminalFrame swingTerminal) {
    
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

        render(map.update());
        render(playerStatus.update());
        render(textBoxList.update());
        render(commandLine.update());

        screen.refresh();
    
    }

    public void render(ArrayList<Object> objects) {

        if(objects != null && !objects.isEmpty()) {

            int canvasPosX   = (int) objects.get(0);
            int canvasPosY   = (int) objects.get(1);
            char[][] content = (char[][]) objects.get(2);

            for(int y = 0; y < content.length; y++) {

                for(int x = 0; x < content[y].length; x++) {
            
                    if(content[y][x] != '\u0000' && !isControlCharacter(content[y][x])) {
                    
                        textGraphics.setCharacter(x + canvasPosX, y + canvasPosY, content[y][x]);
                    
                    } else {

                        textGraphics.setCharacter(x + canvasPosX, y + canvasPosY, ' ');

                    }
                
                }
            
            }

        }

    }

    public void renderTextBox(TextBox textBox) {
        
        int startX = textBox.getBoxPosX();
        int startY = textBox.getBoxPosY();
        ArrayList<String> window = textBox.getWindow();
    
        for(int y = 0; y < window.size(); y++) {
            
            String line = window.get(y);
            
            for(int x = 0; x < line.length(); x++) {
            
                textGraphics.putString(startX + x, startY + y, String.valueOf(line.charAt(x)));
            
            }
        
        }
    
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
    
    public void clearTextField(String backgroundColor, String foregroundColor, int height) {
    
        this.textGraphics.setBackgroundColor(TextColor.ANSI.valueOf(backgroundColor));
        this.textGraphics.setForegroundColor(TextColor.ANSI.valueOf(foregroundColor));
        this.textGraphics.fillRectangle(new TerminalPosition(40, height), new TerminalSize(80, 8), ' ');
    
    }

    public void printRoomDescription(String description, int height) {
    
        clearTextField("BLACK", "CYAN_BRIGHT", height);
        String[] rows = description.split("\n");
    
        for(int y = 0; y < rows.length; y++) {
    
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

        for(int y = 0; y < rows.length; y++) {

            this.textGraphics.putString(40, height + y, rows[y]);

        }

    }

    public void printDescription(String description, int height) {

        clearTextField("BLACK", "CYAN_BRIGHT", height);
        String[] rows = description.split("\n");

        for(int y = 0; y < rows.length; y++) {

            this.textGraphics.putString(40, height + y, rows[y]);

        }

    }

}