package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.InventoryPage;

/**
 * Hamburger menu - opening and closing.
 */
public class Test23HamburgerMenu extends BaseTest {

    @Test
    @DisplayName("The hamburger menu opens, lists its entries and closes again")
    void hamburgerMenuOpensAndCloses() {
        InventoryPage inventoryPage = loginAsStandardUser();

        // Open the menu.
        inventoryPage.openHamburgerMenu();
        assertTrue(inventoryPage.isCloseButtonVisible(), "the menu is open");

        // Verify the menu has all its entries.
        assertEquals(5, inventoryPage.getMenuItemCount(), "the menu lists five entries");

        // Verify the menu items are present.
        assertTrue(inventoryPage.isInventoryLinkVisible());
        assertTrue(inventoryPage.isAboutLinkVisible());
        assertTrue(inventoryPage.isLogoutLinkVisible());

        // Close the menu.
        inventoryPage.closeHamburgerMenu();
        assertFalse(inventoryPage.isCloseButtonVisible(), "the menu is closed");
    }
}