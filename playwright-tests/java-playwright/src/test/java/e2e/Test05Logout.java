package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;
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
        LoginPage loginPage = new LoginPage(page);
        Assertions.assertTrue(loginPage.getUsernameInput().isVisible());
        Assertions.assertTrue(loginPage.getPasswordInput().isVisible());
        Assertions.assertTrue(loginPage.getLoginButton().isVisible());

        // The inventory page is not reachable any more
        Assertions.assertFalse(inventoryPage.getHeaderTitle().isVisible());
    }
}
