package e2e;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;

/**
 * Logout - signing out of the account.
 */
public class Test05Logout extends BaseTest {

    @Test
    @DisplayName("Logout returns to the login form")
    void logoutReturnsToTheLoginForm() {
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = loginAsStandardUser();

        // The menu is the only way out of an authenticated session.
        inventoryPage.clickLogout();

        // Verify we're back on the login page
        assertTrue(loginPage.isLoginFormVisible(), "back on login page");
        assertFalse(inventoryPage.isProductsVisible(), "the catalogue is gone");
    }
}