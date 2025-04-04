package org.myrdn.adventure;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

public class Renderer {

    private final Font myFont;
    private final SwingTerminalFontConfiguration myFontConfiguration;
    private Terminal terminal;
    private Screen screen;
    private final ArrayList<Label> labels;
    private final ArrayList<Panel> panels;
    private final ArrayList<BasicWindow> windows;
    private final DefaultWindowManager defaultWindowManager;
    private final EmptySpace background;
    private MultiWindowTextGUI gui;
    private final TerminalSize size;
    private final TextColor.RGB brown = new TextColor.RGB(48,16,0); 

    public Renderer() throws IOException {
        this.size = new TerminalSize(120, 40);
        this.myFont = new Font("JetBrains Mono", Font.PLAIN, 14);
        this.myFontConfiguration = SwingTerminalFontConfiguration.newInstance(myFont);
        this.labels = new ArrayList<>();
        this.panels = new ArrayList<>();
        this.windows = new ArrayList<>();
        this.defaultWindowManager = new DefaultWindowManager();
        this.background = new EmptySpace(brown);
    }

    public void initScreen() throws IOException {
        this.terminal = new DefaultTerminalFactory().setTerminalEmulatorFontConfiguration(myFontConfiguration).setInitialTerminalSize(size).createTerminal();
        this.screen = new TerminalScreen(this.terminal);
        this.screen.startScreen();
    }

    public void addComponentToPanel(String text, String title, int panel) {
        this.labels.add(new Label(text));
        this.panels.get(panel).addComponent(this.labels.get(this.labels.size()-1)).withBorder(Borders.singleLine(title));
    }

    public void addNewWindow() {
        this.windows.add(new BasicWindow());
    }

    public void addNewPanel() {
        this.panels.add(new Panel());
    }

    public void initGUI() {
        this.gui = new MultiWindowTextGUI(this.screen, this.defaultWindowManager, this.background);
    }

    public void setComponents() {
        for(int i = 0; i < this.windows.size(); i++) {
            for(int j = 0; j < this.panels.size(); j++) {
                this.windows.get(i).setComponent(this.panels.get(j));
            }
        }
    }

    public void startGUI() {
        addNewWindow();
        initGUI();
        setComponents();
        for(BasicWindow LocalWindow : this.windows) {
            this.gui.addWindowAndWait(LocalWindow);
        }
    }
}
