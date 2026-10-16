package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Logout - signing out of the account
 */
class Test05Logout extends BaseTest {

    @Test
    void logoutSigningOutOfTheAccount(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        // Ensure that we are on the inventory page
        Assertions.assertTrue(page.url().matches(".*inventory\\.html.*"));

        // Open the menu and click Logout
        inventoryPage.openHamburgerMenu();
        inventoryPage.clickLogout();

        // Logging out brings the login form back
        Assertions.assertTrue(page.locator("#user-name").isVisible());
        Assertions.assertTrue(page.locator("#password").isVisible());
        Assertions.assertTrue(page.locator("#login-button").isVisible());

        // The inventory page is not reachable any more
        Assertions.assertFalse(inventoryPage.getHeaderTitle().isVisible());
    }
}
