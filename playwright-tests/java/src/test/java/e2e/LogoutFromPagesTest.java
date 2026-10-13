package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;

/**
 * Verify logout functionality from different pages.
 */
public class LogoutFromPagesTest extends BaseTest {

    @Test
    @DisplayName("Logout from the inventory page blocks the catalogue until login again")
    void logoutFromInventoryPage() {
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = loginAsStandardUser();

        inventoryPage.openHamburgerMenu();
        inventoryPage.clickLogout();

        // Logout really happened: the login form is back and the inventory is gone.
        loginPage.waitForUsernameInputVisible();
        assertTrue(loginPage.isUsernameInputVisible());
        assertTrue(loginPage.isLoginButtonVisible());
        assertFalse(inventoryPage.isHeaderTitleVisible(), "the catalogue is gone");

        // The inventory page is no longer reachable without logging in again.
        openPath("/inventory.html");
        loginPage.waitForUsernameInputVisible();
        assertTrue(loginPage.isUsernameInputVisible(), "the inventory page redirects to login");
    }

    @Test
    @DisplayName("Logout from the cart page and log back in")
    void logoutFromCartPage() {
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        inventoryPage.addProductToCart(0);
        inventoryPage.waitForCartBadgeCount(1);

        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        inventoryPage.openHamburgerMenu();
        inventoryPage.clickLogout();

        loginPage.waitForUsernameInputVisible();
        assertTrue(loginPage.isUsernameInputVisible());

        // Logging back in returns to the inventory page.
        loginPage.login(TestConfig.USERNAME, TestConfig.PASSWORD);
        inventoryPage.waitForPageLoad();
        assertTrue(driver.getCurrentUrl().contains("inventory.html"),
            "logging back in lands on the inventory page");
        assertEquals("Products", inventoryPage.getTitleText(), "the catalogue is rendered again");
    }
}
