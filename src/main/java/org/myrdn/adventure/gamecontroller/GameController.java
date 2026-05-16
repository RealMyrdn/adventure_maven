package org.myrdn.adventure.gamecontroller;

import com.badlogic.gdx.Gdx;

import org.myrdn.adventure.AdventureGame;
import org.myrdn.adventure.datahandler.DataHandler;
import org.myrdn.adventure.datahandler.Dungeon;
import org.myrdn.adventure.datahandler.Enemy;
import org.myrdn.adventure.datahandler.GameObject;
import org.myrdn.adventure.datahandler.ItemObject;
import org.myrdn.adventure.datahandler.Player;
import org.myrdn.adventure.datahandler.Room;
import org.myrdn.adventure.datahandler.SaveGame;
import org.myrdn.adventure.renderer.TextBoxList;
import org.myrdn.adventure.screens.MainMenuScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private static final int SOUTH = 0b0001;
    private static final int WEST  = 0b0010;
    private static final int NORTH = 0b0100;
    private static final int EAST  = 0b1000;

    private final AdventureGame game;
    private final Player player;
    private final Dungeon dungeon;
    private final DataHandler datahandler;
    private final SaveGame savegame;
    private final boolean autosaveEnabled;
    private final int xSize;
    private final int ySize;

    private TextBoxList textBoxList;
    private GameObject activeObject;
    private long lastAutoSaveTime;

    public GameController(AdventureGame game, TextBoxList textBoxList, Player player, Dungeon dungeon,
                          DataHandler datahandler, SaveGame savegame, boolean autosaveEnabled,
                          int xSize, int ySize) {
        this.game            = game;
        this.textBoxList     = textBoxList;
        this.player          = player;
        this.dungeon         = dungeon;
        this.datahandler     = datahandler;
        this.savegame        = savegame;
        this.autosaveEnabled = autosaveEnabled;
        this.xSize           = xSize;
        this.ySize           = ySize;
        this.lastAutoSaveTime = System.currentTimeMillis();
        this.activeObject    = null;
    }

    public void setTextBoxList(TextBoxList textBoxList) {
        this.textBoxList = textBoxList;
    }

    public void tick() {
        if (autosaveEnabled) {
            checkAutoSave();
        }
    }

    public void executeInstructions(ArrayList<String> instructions) throws IOException {
        if (instructions == null || instructions.isEmpty()) {
            showMessage("Bitte gib einen Befehl ein. Tippe 'hilfe' für Hilfe.");
            return;
        }

        String command = instructions.get(0).toLowerCase();
        Room currentRoom = dungeon.getRoom(player.getX(), player.getY());
        int roomType = currentRoom.getRoomConnections();

        logger.debug("Executing command: {}", command);

        switch (command) {
            case "exit", "beenden"              -> handleExit();
            case "menu", "menü"                 -> returnToMenu();
            case "gehe"                         -> handleMovement(instructions, roomType);
            case "untersuche"                   -> handleExamine(instructions);
            case "nimm"                         -> handleTake(instructions);
            case "inventar"                     -> handleInventory();
            case "benutze"                      -> handleUse(instructions);
            case "speichern"                    -> handleSave();
            case "angreifen", "angriff",
                 "kämpfen"                      -> handleAttack();
            case "fliehen", "flucht"            -> handleFlee();
            case "hilfe"                        -> handleHelp();
            default -> showMessage("Unbekannter Befehl: '" + command + "'. Tippe 'hilfe' für verfügbare Befehle.");
        }
    }

    private void handleExit() {
        try {
            logger.info("Saving game...");
            datahandler.saveGame(savegame);
            logger.info("Game saved successfully");
        } catch (IOException e) {
            logger.error("Fehler beim Speichern des Spiels", e);
        }
        logger.info("Exiting game");
        Gdx.app.exit();
    }

    private void returnToMenu() {
        try {
            logger.info("Saving game before returning to menu...");
            datahandler.saveGame(savegame);
            logger.info("Game saved successfully");
        } catch (IOException e) {
            logger.error("Fehler beim Speichern des Spiels", e);
        }
        game.setScreen(new MainMenuScreen(game));
    }

    private void handleSave() {
        try {
            datahandler.saveGame(savegame);
            showMessage("Spiel wurde gespeichert!");
            logger.info("Game saved successfully");
        } catch (IOException e) {
            logger.error("Fehler beim Speichern", e);
            showMessage("Fehler beim Speichern!");
        }
    }

    private void handleMovement(ArrayList<String> instructions, int roomType) {
        if (instructions.size() < 2) {
            showMessage("Wohin möchtest du gehen? (nord/süd/ost/west)");
            return;
        }

        String direction = instructions.get(1).toLowerCase();

        switch (direction) {
            case "süd",  "s" -> move(0,  1, SOUTH, roomType);
            case "west", "w" -> move(-1, 0, WEST,  roomType);
            case "nord", "n" -> move(0, -1, NORTH, roomType);
            case "ost",  "o" -> move(1,  0, EAST,  roomType);
            default -> showMessage("Ungültige Richtung: '" + direction + "'. Nutze: nord, süd, ost oder west.");
        }
    }

    private void handleExamine(ArrayList<String> instructions) {
        if (instructions.size() < 2) {
            showMessage("Was möchtest du untersuchen?");
            return;
        }

        String target = instructions.get(1).toLowerCase();
        Room room = dungeon.getRoom(player.getX(), player.getY());

        if ("raum".equals(target)) {
            textBoxList.addTextBox(25, 13, 45, 10, room.getRoomObjects(), "Entdeckungen");
            return;
        }

        for (GameObject object : room.getObjects()) {
            if (object.getName().toLowerCase().contains(target)) {
                StringBuilder sb = new StringBuilder();
                sb.append(object.getDescription()).append("\n");
                for (ItemObject item : object.getHiddenItems()) {
                    sb.append(item.getName()).append("\n");
                }
                textBoxList.addTextBox(65, 12, 45, 15, sb.toString(), "Fund");
                activeObject = object;
                return;
            }
        }

        showMessage("'" + target + "' nicht gefunden. Versuche 'untersuche raum'.");
    }

    private void handleTake(ArrayList<String> instructions) {
        if (instructions.size() < 2) {
            showMessage("Was möchtest du nehmen?");
            return;
        }

        if (activeObject == null) {
            showMessage("Untersuche zuerst ein Objekt.");
            return;
        }

        String itemName = instructions.get(1).toLowerCase();
        ItemObject foundItem = null;

        for (ItemObject item : activeObject.getHiddenItems()) {
            if (item.getName().toLowerCase().contains(itemName)) {
                foundItem = item;
                break;
            }
        }

        if (foundItem == null) {
            showMessage("'" + instructions.get(1) + "' ist hier nicht zu finden.");
            return;
        }

        player.addItemInv(foundItem);
        activeObject.removeHiddenItem(foundItem);

        textBoxList.removeLast();

        StringBuilder sb = new StringBuilder();
        sb.append(activeObject.getDescription()).append("\n");
        if (activeObject.getHiddenItems().isEmpty()) {
            sb.append("\nHier ist nichts mehr.");
        } else {
            for (ItemObject item : activeObject.getHiddenItems()) {
                sb.append(item.getName()).append("\n");
            }
        }

        textBoxList.addTextBox(65, 12, 45, 15, sb.toString(), "Fund");
        showMessage("Du hast '" + foundItem.getName() + "' aufgenommen.");
    }

    private void handleInventory() {
        textBoxList.addTextBox(20, 10, 30, 10, player.getIventoryAsList(), "Inventar");
    }

    private void handleUse(ArrayList<String> instructions) {
        if (instructions == null || instructions.size() < 2) {
            showMessage("Was möchtest du benutzen?");
            return;
        }

        String itemName = instructions.get(1).toLowerCase();
        ItemObject item = player.getItemFromInventory(itemName);

        if (item == null) {
            showMessage("Du hast keinen Gegenstand namens '" + itemName + "' im Inventar.");
            return;
        }

        if (item.isUsedUp()) {
            showMessage("'" + item.getName() + "' ist aufgebraucht.");
            return;
        }

        logger.debug("Using item: {} with effect {}", itemName, item.getEffectType());

        Room currentRoom = dungeon.getRoom(player.getX(), player.getY());
        StringBuilder result = new StringBuilder();
        result.append("Du benutzt: ").append(item.getName()).append("\n\n");

        switch (item.getEffectType()) {
            case HEAL -> {
                int oldHealth = player.getHealth();
                player.setHealth(player.getHealth() + item.getEffectValue());
                int actualHeal = player.getHealth() - oldHealth;
                result.append("Du wirst um ").append(actualHeal).append(" HP geheilt.\n");
                result.append("HP: ").append(player.getHealth()).append("/").append(player.getMaxHealth());
                item.use();
            }
            case ATTACK_BOOST -> {
                int boost = item.getEffectValue();
                player.setAttack(player.getAttack() + boost);
                result.append("Dein Angriff steigt um ").append(boost).append("!\n");
                result.append("Angriff: ").append(player.getAttack());
                if (item.isConsumable()) item.use();
            }
            case DEFENSE_BOOST -> {
                int boost = item.getEffectValue();
                player.setMaxHealth(player.getMaxHealth() + boost);
                player.setHealth(player.getHealth() + boost);
                result.append("Deine Verteidigung steigt!\n");
                result.append("Max HP: ").append(player.getMaxHealth());
                if (item.isConsumable()) item.use();
            }
            case DAMAGE -> {
                if (!currentRoom.hasEnemy()) {
                    result.append("Kein Gegner in der Nähe!");
                } else {
                    Enemy enemy = currentRoom.getEnemy();
                    enemy.takeDamage(item.getEffectValue());
                    result.append("Du verursachst ").append(item.getEffectValue())
                          .append(" Schaden an ").append(enemy.getName()).append("!\n");
                    if (!enemy.isAlive()) {
                        result.append("\nDer ").append(enemy.getName()).append(" wurde besiegt!");
                        currentRoom.removeEnemy();
                    } else {
                        result.append("Gegner HP: ").append(enemy.getHealth())
                              .append("/").append(enemy.getMaxHealth());
                    }
                    item.use();
                }
            }
            case FLEE -> {
                if (!currentRoom.hasEnemy()) {
                    result.append("Du bist nicht in Gefahr.");
                } else {
                    flee(currentRoom.getRoomConnections());
                    result.append("Du fliehst erfolgreich!");
                    item.use();
                    textBoxList.clearList();
                    textBoxList.addTextBox(40, 15, 40, 10,
                            dungeon.getRoom(player.getX(), player.getY()).getRoomInfo(), "Geflohen!");
                }
            }
            case NONE -> result.append(item.getDescription());
        }

        if (item.isUsedUp()) {
            player.removeItemInv(item);
            result.append("\n\n(").append(item.getName()).append(" wurde verbraucht)");
        }

        textBoxList.addTextBox(35, 10, 50, 14, result.toString(), "Item benutzt");
    }

    private void handleHelp() {
        StringBuilder help = new StringBuilder();
        help.append("Verfügbare Befehle:\n\n");
        help.append("gehe [richtung] - Bewege dich\n");
        help.append("untersuche [objekt/raum] - Untersuche\n");
        help.append("nimm [gegenstand] - Aufnehmen\n");
        help.append("inventar - Dein Inventar\n");
        help.append("benutze [gegenstand] - Benutzen\n");
        help.append("angreifen - Gegner angreifen\n");
        help.append("fliehen - Vor Gegner fliehen\n");
        help.append("speichern - Spiel speichern\n");
        help.append("menü - Zurück zum Hauptmenü\n");
        help.append("hilfe - Diese Hilfe\n");
        help.append("exit - Beenden\n");
        textBoxList.addTextBox(30, 8, 50, 24, help.toString(), "Hilfe");
    }

    private void handleAttack() {
        Room currentRoom = dungeon.getRoom(player.getX(), player.getY());

        if (!currentRoom.hasEnemy()) {
            showMessage("Hier gibt es keinen Gegner zum Angreifen.");
            return;
        }

        Enemy enemy = currentRoom.getEnemy();
        int playerDamage = player.getAttack();
        int actualDamage = Math.max(1, playerDamage - enemy.getDefense());
        enemy.takeDamage(playerDamage);

        StringBuilder combatLog = new StringBuilder();
        combatLog.append("Kampf gegen ").append(enemy.getName()).append("!\n\n");
        combatLog.append("Du greifst an und verursachst ").append(actualDamage).append(" Schaden.\n");

        if (!enemy.isAlive()) {
            combatLog.append("\nDu hast den ").append(enemy.getName()).append(" besiegt!");
            currentRoom.removeEnemy();
            textBoxList.addTextBox(35, 12, 50, 12, combatLog.toString(), "Sieg!");
            return;
        }

        int enemyDamage = enemy.getAttack();
        player.setHealth(player.getHealth() - enemyDamage);

        combatLog.append("Der ").append(enemy.getName())
                 .append(" greift zurück und verursacht ").append(enemyDamage).append(" Schaden.\n\n");
        combatLog.append("Gegner: ").append(enemy.getHealth()).append("/").append(enemy.getMaxHealth()).append(" HP\n");
        combatLog.append("Du: ").append(player.getHealth()).append("/").append(player.getMaxHealth()).append(" HP");

        if (player.getHealth() <= 0) {
            combatLog.append("\n\nDu wurdest besiegt...");
            textBoxList.addTextBox(35, 10, 50, 14, combatLog.toString(), "Niederlage");
            Gdx.app.postRunnable(() -> game.setScreen(new MainMenuScreen(game)));
        } else {
            textBoxList.addTextBox(35, 10, 50, 14, combatLog.toString(), "Kampf");
        }
    }

    private void handleFlee() {
        Room currentRoom = dungeon.getRoom(player.getX(), player.getY());

        if (!currentRoom.hasEnemy()) {
            showMessage("Hier gibt es nichts, vor dem du fliehen müsstest.");
            return;
        }

        Enemy enemy = currentRoom.getEnemy();

        if (new Random().nextInt(100) < 50) {
            flee(currentRoom.getRoomConnections());
            textBoxList.clearList();
            textBoxList.addTextBox(40, 15, 40, 10,
                    dungeon.getRoom(player.getX(), player.getY()).getRoomInfo(), "Geflohen!");
            showMessage("Du bist erfolgreich geflohen!");
        } else {
            int enemyDamage = enemy.getAttack();
            player.setHealth(player.getHealth() - enemyDamage);

            StringBuilder msg = new StringBuilder();
            msg.append("Flucht fehlgeschlagen!\n\n");
            msg.append("Der ").append(enemy.getName()).append(" trifft dich beim Fliehen!\n");
            msg.append("Du erleidest ").append(enemyDamage).append(" Schaden.\n\n");
            msg.append("Du: ").append(player.getHealth()).append("/").append(player.getMaxHealth()).append(" HP");

            if (player.getHealth() <= 0) {
                msg.append("\n\nDu wurdest besiegt...");
                textBoxList.addTextBox(35, 12, 50, 12, msg.toString(), "Flucht");
                Gdx.app.postRunnable(() -> game.setScreen(new MainMenuScreen(game)));
                return;
            }

            textBoxList.addTextBox(35, 12, 50, 12, msg.toString(), "Flucht");
        }
    }

    private void flee(int roomType) {
        int x = player.getX();
        int y = player.getY();
        if      ((roomType & NORTH) != 0 && y > 0)           player.setPosition(x, y - 1);
        else if ((roomType & SOUTH) != 0 && y < ySize - 1)   player.setPosition(x, y + 1);
        else if ((roomType & WEST)  != 0 && x > 0)           player.setPosition(x - 1, y);
        else if ((roomType & EAST)  != 0 && x < xSize - 1)   player.setPosition(x + 1, y);
    }

    private void move(int dx, int dy, int directionBit, int value) {
        if ((value & directionBit) != 0) {
            int newX = player.getX() + dx;
            int newY = player.getY() + dy;

            if (newY >= 0 && newY < ySize && newX >= 0 && newX < xSize) {
                player.setPosition(newX, newY);
                textBoxList.clearList();
                textBoxList.addTextBox(40, 15, 40, 10,
                        dungeon.getRoom(player.getX(), player.getY()).getRoomInfo(), "Neuer Raum");
                activeObject = null;
            }
        } else {
            showMessage("In diese Richtung gibt es keinen Ausgang.");
        }
    }

    private void showMessage(String message) {
        textBoxList.addTextBox(40, 20, 40, 8, message, "Hinweis");
    }

    private void checkAutoSave() {
        long intervalMs = 5 * 60 * 1000L;
        if (System.currentTimeMillis() - lastAutoSaveTime >= intervalMs) {
            performAutoSave();
            lastAutoSaveTime = System.currentTimeMillis();
        }
    }

    private void performAutoSave() {
        try {
            logger.info("Performing auto-save...");
            datahandler.saveGame(savegame);
            logger.info("Auto-save completed successfully");
        } catch (IOException e) {
            logger.error("Auto-save failed", e);
        }
    }
}
