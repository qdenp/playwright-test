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
public class Test06LogoutFromPages extends BaseTest {

    @Test
    @DisplayName("Logout from the inventory page blocks the catalogue until login again")
    void logoutFromInventoryPage() {
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = loginAsStandardUser();

        inventoryPage.clickLogout();

        // Logout really happened: the login form is back and the inventory is gone.
        assertTrue(loginPage.isLoginFormVisible(), "back on login page");
        assertFalse(inventoryPage.isProductsVisible(), "the catalogue is gone");

        // The inventory page is no longer reachable without logging in again.
        openPath(TestConfig.INVENTORY_PAGE_PATH);
        assertTrue(loginPage.isLoginFormVisible(), "the inventory page redirects to login");
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

        inventoryPage.clickLogout();

        // Logout really happened: the login form is back and the inventory is gone.
        assertTrue(loginPage.isLoginFormVisible(), "back on login page");
        assertFalse(inventoryPage.isProductsVisible(), "the catalogue is gone");

         // Logging back in returns to the inventory page.
         // Chrome caches the login page and keystrokes may not land in the
         // fields on a second attempt in the same session.
         restartBrowser();
         inventoryPage = loginAsStandardUser();
        assertTrue(driver.getCurrentUrl().contains("inventory.html"),
            "logging back in lands on the inventory page");
        assertEquals(TestConfig.INVENTORY_PAGE_TITLE, inventoryPage.getTitleText(), "the catalogue is rendered again");
    }
}