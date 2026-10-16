package e2e;

import com.microsoft.playwright.*;
import pageobjects.CartPage;
import pageobjects.CheckoutPage;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;
import org.junit.jupiter.api.*;
import base.BaseTest;

/**
 * Browser navigation tests
 */
class Test26BrowserNavigation extends BaseTest {

    @Test
    void shouldHandleBrowserBackButtonFromCartToInventory(Page page) {
        LoginPage loginPage = new LoginPage(page);
        InventoryPage inventoryPage = new InventoryPage(page);
        CartPage cartPage = new CartPage(page);

        loginPage.goTo();
        loginPage.login();
        inventoryPage.waitForPageLoad();

        inventoryPage.addProductToCart(0);
        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        // Go back to inventory
        page.goBack();
        inventoryPage.waitForPageLoad();

        Assertions.assertTrue(page.url().matches(".*inventory.*"));
        Assertions.assertTrue(inventoryPage.getHeaderTitle().textContent().contains("Products"));
    }

    @Test
    void shouldHandleBrowserForwardButtonAfterBack(Page page) {
        LoginPage loginPage = new LoginPage(page);
        InventoryPage inventoryPage = new InventoryPage(page);
        CartPage cartPage = new CartPage(page);

        loginPage.goTo();
        loginPage.login();
        inventoryPage.waitForPageLoad();

        inventoryPage.addProductToCart(0);
        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        // Go back
        page.goBack();
        inventoryPage.waitForPageLoad();

        // Go forward
        page.goForward();
        cartPage.waitForPageLoad();

        Assertions.assertTrue(page.url().matches(".*cart.*"));
    }

    @Test
    void shouldHandleBackFromCheckoutToCart(Page page) {
        LoginPage loginPage = new LoginPage(page);
        InventoryPage inventoryPage = new InventoryPage(page);
        CartPage cartPage = new CartPage(page);
        CheckoutPage checkoutPage = new CheckoutPage(page);

        loginPage.goTo();
        loginPage.login();
        inventoryPage.waitForPageLoad();

        inventoryPage.addProductToCart(0);
        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        cartPage.checkout();
        checkoutPage.waitForPageLoad();

        // Go back to cart
        page.goBack();
        cartPage.waitForPageLoad();

        Assertions.assertTrue(page.url().matches(".*cart.*"));
    }

    @Test
    void shouldPreserveCartStateAfterMultipleBackForward(Page page) {
        LoginPage loginPage = new LoginPage(page);
        InventoryPage inventoryPage = new InventoryPage(page);
        CartPage cartPage = new CartPage(page);

        loginPage.goTo();
        loginPage.login();
        inventoryPage.waitForPageLoad();

        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(1);
        Assertions.assertEquals("2", inventoryPage.getCartBadgeCountLocator().textContent());

        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        // Navigate back and forth
        page.goBack();
        inventoryPage.waitForPageLoad();
        page.goForward();
        cartPage.waitForPageLoad();

        // Cart should still have 2 items
        Assertions.assertEquals("2", inventoryPage.getCartBadgeCountLocator().textContent());
    }
}
