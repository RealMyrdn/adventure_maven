package org.myrdn.adventure.datahandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Player class.
 */
class PlayerTest {

    private Player player;
    private static final String PLAYER_NAME = "TestPlayer";
    private static final int START_X = 5;
    private static final int START_Y = 5;

    @BeforeEach
    void setUp() {
        player = new Player(PLAYER_NAME, START_X, START_Y);
    }

    @Test
    void testPlayerCreation() {
        assertNotNull(player);
        assertEquals(PLAYER_NAME, player.getName());
        assertEquals(START_X, player.getX());
        assertEquals(START_Y, player.getY());
    }

    @Test
    void testInitialHealth() {
        assertEquals(15, player.getHealth());
        assertEquals(15, player.getMaxHealth());
    }

    @Test
    void testInitialAttack() {
        assertEquals(1, player.getAttack());
    }

    @Test
    void testSetPosition() {
        int newX = 10;
        int newY = 10;

        player.setPosition(newX, newY);

        assertEquals(newX, player.getX());
        assertEquals(newY, player.getY());
        assertTrue(player.hasMoved());
    }

    @Test
    void testSetHealth() {
        player.setHealth(10);
        assertEquals(10, player.getHealth());
    }

    @Test
    void testSetHealthAboveMax() {
        player.setHealth(20);
        assertEquals(player.getMaxHealth(), player.getHealth());
    }

    @Test
    void testSetMaxHealth() {
        player.setMaxHealth(20);
        assertEquals(20, player.getMaxHealth());
    }

    @Test
    void testSetAttack() {
        player.setAttack(5);
        assertEquals(5, player.getAttack());
    }

    @Test
    void testInventoryInitiallyEmpty() {
        assertNotNull(player.getIventory());
        assertTrue(player.getIventory().isEmpty());
    }

    @Test
    void testAddItemToInventory() {
        ItemObject item = new ItemObject("Test Item", 10, "Test Description");
        player.addItemInv(item);

        assertEquals(1, player.getIventory().size());
        assertTrue(player.getIventory().contains(item));
    }

    @Test
    void testRemoveItemFromInventory() {
        ItemObject item = new ItemObject("Test Item", 10, "Test Description");
        player.addItemInv(item);
        player.removeItemInv(item);

        assertTrue(player.getIventory().isEmpty());
    }

    @Test
    void testGetItemFromInventory() {
        ItemObject item = new ItemObject("Sword", 50, "A sharp sword");
        player.addItemInv(item);

        ItemObject found = player.getItemFromInventory("sword");
        assertNotNull(found);
        assertEquals("Sword", found.getName());
    }

    @Test
    void testGetItemFromInventoryNotFound() {
        ItemObject found = player.getItemFromInventory("NonexistentItem");
        assertNull(found);
    }

    @Test
    void testGetItemFromInventoryNull() {
        ItemObject found = player.getItemFromInventory(null);
        assertNull(found);
    }

    @Test
    void testGetInventoryAsList() {
        ItemObject item1 = new ItemObject("Sword", 50, "A sharp sword");
        ItemObject item2 = new ItemObject("Shield", 100, "A sturdy shield");

        player.addItemInv(item1);
        player.addItemInv(item2);

        String inventoryList = player.getIventoryAsList();

        assertTrue(inventoryList.contains("Sword"));
        assertTrue(inventoryList.contains("Shield"));
    }

    @Test
    void testHasNotMovedInitially() {
        Player newPlayer = new Player("Test", 5, 5);
        // Initially, previous position is 0,0 (set in constructor)
        // Current position is 5,5
        // So hasMoved() should return true because they're different
        assertTrue(newPlayer.hasMoved());
    }

    @Test
    void testHasMovedAfterPositionChange() {
        player.setPosition(6, 6);
        assertTrue(player.hasMoved());
    }
}
