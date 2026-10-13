package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;

/**
 * Hamburger menu - opening and closing.
 */
public class HamburgerMenuTest extends BaseTest {

    @Test
    @DisplayName("The hamburger menu opens, lists its entries and closes again")
    void hamburgerMenuOpensAndCloses() {
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = loginAsStandardUser();

        // Open the menu.
        loginPage.openHamburgerMenu();
        assertTrue(loginPage.isHamburgerMenuOpen(), "the menu is open");

        // Verify the menu has all its entries.
        loginPage.waitForMenuOverlayVisible();
        assertEquals(5, inventoryPage.getMenuItemCount(), "the menu lists five entries");

        // Verify the menu items are present.
        assertTrue(inventoryPage.isInventoryLinkVisible());
        assertTrue(inventoryPage.isAboutLinkVisible());
        assertTrue(inventoryPage.isLogoutLinkVisible());

        // Close the menu.
        loginPage.closeHamburgerMenu();
        loginPage.waitForHamburgerMenuClosed();
        assertFalse(loginPage.isHamburgerMenuOpen(), "the menu is closed");
    }
}
