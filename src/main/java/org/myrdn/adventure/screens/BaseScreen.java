package org.myrdn.adventure.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;

import org.myrdn.adventure.AdventureGame;

public abstract class BaseScreen implements Screen {

    protected static final int TERMINAL_COLS = 120;
    protected static final int TERMINAL_ROWS = 40;

    protected final AdventureGame game;
    protected SpriteBatch batch;
    protected BitmapFont font;
    protected char[][] screenBuffer;
    protected int charWidth = 10;
    protected int charHeight = 16;

    public BaseScreen(AdventureGame game) {
        this.game = game;
        this.batch = game.getBatch();
        this.font = game.getFont();
        this.screenBuffer = new char[TERMINAL_ROWS][TERMINAL_COLS];
    }

    protected void clearScreenBuffer() {
        for (int y = 0; y < TERMINAL_ROWS; y++) {
            for (int x = 0; x < TERMINAL_COLS; x++) {
                screenBuffer[y][x] = ' ';
            }
        }
    }

    protected void drawScreenBuffer() {
        int windowHeight = Gdx.graphics.getHeight();

        for (int y = 0; y < TERMINAL_ROWS; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < TERMINAL_COLS; x++) {
                line.append(screenBuffer[y][x]);
            }
            font.draw(batch, line.toString(), 0, windowHeight - (y * charHeight));
        }
    }

    protected void drawText(String text, int col, int row) {
        for (int i = 0; i < text.length() && col + i < TERMINAL_COLS; i++) {
            screenBuffer[row][col + i] = text.charAt(i);
        }
    }

    protected void drawBox(int x, int y, int width, int height, String title) {
        // Top border
        screenBuffer[y][x] = '╔';
        screenBuffer[y][x + width - 1] = '╗';

        // Title centered
        int titleStart = x + (width - title.length() - 4) / 2;
        for (int i = x + 1; i < x + width - 1; i++) {
            if (i == titleStart) {
                screenBuffer[y][i] = '╡';
            } else if (i == titleStart + 1) {
                screenBuffer[y][i] = ' ';
            } else if (i > titleStart + 1 && i <= titleStart + 1 + title.length()) {
                screenBuffer[y][i] = title.charAt(i - titleStart - 2);
            } else if (i == titleStart + title.length() + 2) {
                screenBuffer[y][i] = ' ';
            } else if (i == titleStart + title.length() + 3) {
                screenBuffer[y][i] = '╞';
            } else {
                screenBuffer[y][i] = '═';
            }
        }

        // Bottom border
        screenBuffer[y + height - 1][x] = '╚';
        screenBuffer[y + height - 1][x + width - 1] = '╝';
        for (int i = x + 1; i < x + width - 1; i++) {
            screenBuffer[y + height - 1][i] = '═';
        }

        // Side borders
        for (int i = y + 1; i < y + height - 1; i++) {
            screenBuffer[i][x] = '║';
            screenBuffer[i][x + width - 1] = '║';
        }
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
