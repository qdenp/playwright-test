package e2e;

import com.microsoft.playwright.*;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Hamburger menu - opening and closing
 */
class Test23HamburgerMenu extends BaseTest {

    @Test
    void hamburgerMenuOpeningAndClosing(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        // Open the menu
        inventoryPage.openHamburgerMenu();
        Assertions.assertTrue(inventoryPage.isCloseButtonVisible());

        // Verify the menu has 5 items
        Assertions.assertEquals(5, inventoryPage.getMenuItemCount());

        // Verify the menu items are present (using JS visibility checks)
        Assertions.assertTrue(inventoryPage.isInventoryLinkVisible());
        Assertions.assertTrue(inventoryPage.isAboutLinkVisible());
        Assertions.assertTrue(inventoryPage.isLogoutLinkVisible());

        // Close the menu
        inventoryPage.closeHamburgerMenu();
        Assertions.assertFalse(inventoryPage.isCloseButtonVisible());
    }
}
