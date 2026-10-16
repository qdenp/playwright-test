package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;
import pageobjects.CartPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Verify logout functionality from different pages
 */
class Test06LogoutFromPages extends BaseTest {

    @Test
    void shouldLogoutFromTheInventoryPage(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        LoginPage loginPage = authenticatedPage.loginPage;
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        inventoryPage.openHamburgerMenu();
        inventoryPage.clickLogout();

        // Logout really happened: the login form is back and the inventory is gone
        Assertions.assertTrue(loginPage.getUsernameInput().isVisible());
        Assertions.assertTrue(loginPage.getLoginButton().isVisible());
        Assertions.assertFalse(inventoryPage.getHeaderTitle().isVisible());

        // The inventory page is no longer reachable without logging in again
        Page.NavigateOptions navigateOptions = new Page.NavigateOptions()
                .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED);
        page.navigate("inventory.html", navigateOptions);
        Assertions.assertTrue(loginPage.getUsernameInput().isVisible());
    }

    @Test
    void shouldLogoutFromTheCartPage(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        LoginPage loginPage = authenticatedPage.loginPage;
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        inventoryPage.addProductToCart(0);
        Assertions.assertEquals("1", inventoryPage.getCartBadgeCountLocator().textContent());

        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        inventoryPage.openHamburgerMenu();
        inventoryPage.clickLogout();

        Assertions.assertTrue(loginPage.getUsernameInput().isVisible());
        Assertions.assertTrue(loginPage.getLoginButton().isVisible());

        // Logging back in returns to the inventory page
        loginPage.login();
        inventoryPage.waitForPageLoad();
        Assertions.assertTrue(page.url().matches(".*inventory\\.html.*"));
    }
}
