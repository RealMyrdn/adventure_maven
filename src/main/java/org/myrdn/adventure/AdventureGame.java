package org.myrdn.adventure;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import org.myrdn.adventure.screens.MainMenuScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main LibGDX Application class for the Adventure game.
 * Manages screens and shared resources.
 */
public class AdventureGame extends Game {

    private static final Logger logger = LoggerFactory.getLogger(AdventureGame.class);

    private SpriteBatch batch;
    private BitmapFont font;

    @Override
    public void create() {
        logger.info("Initializing Adventure Game...");

        batch = new SpriteBatch();

        // Load font with extended character set for box-drawing and special characters
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/JetBrainsMono-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        parameter.color = Color.WHITE;
        parameter.mono = true;
        // Include ASCII, Latin Extended, Box Drawing, Block Elements, and German characters
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS
                + "äöüÄÖÜß"
                + "╔╗╚╝═║╡╞┏┓┗┛┃━┳┻┣┫╋╻╸╹╺█"
                + "▓░•→←↑↓>";
        font = fontGenerator.generateFont(parameter);
        fontGenerator.dispose();

        logger.info("Font loaded successfully");

        // Start with main menu
        setScreen(new MainMenuScreen(this));
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public BitmapFont getFont() {
        return font;
    }

    @Override
    public void dispose() {
        logger.info("Disposing Adventure Game...");
        if (batch != null) {
            batch.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        super.dispose();
    }
}
