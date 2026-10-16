package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.CartPage;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Reset App State
 */
class Test28ResetAppState extends BaseTest {

    @Test
    void shouldClearTheCartBadgeWhenResetAppStateIsUsed(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        for (int i = 0; i < 3; i++) {
            inventoryPage.addProductToCart(i);
        }
        Assertions.assertEquals("3", inventoryPage.getCartBadgeCountLocator().textContent());

        inventoryPage.openHamburgerMenu();
        inventoryPage.resetAppState();
        inventoryPage.closeHamburgerMenu();

        // The badge disappears immediately - the cart is empty
        Assertions.assertEquals(0, inventoryPage.getCartBadgeCountLocator().count());
    }

    /**
     * SAUCE_DEMO_BUG: Reset App State requires a page reload to fully work.
     *
     * The test verifies two things that should happen immediately after clicking
     * "Reset App State" but currently require a page reload on the GitHub Pages
     * build:
     *
     * 1. While on the cart page — cart items are removed (not just the badge)
     * 2. While on the inventory page — "Add to Cart" buttons reappear
     *
     * On the real SauceDemo (server-side state) these work instantly.
     * On GitHub Pages the SPA needs a reload, but reload redirects to login.
     *
     * Once the site bug is fixed: remove `test.skip` and uncomment the body.
     */
    @Test
    @Disabled("SAUCE_DEMO_BUG: Reset App State requires a page reload to fully work on GitHub Pages.")
    void shouldClearTheStoredCartNotJustTheBadge(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        for (int i = 0; i < 3; i++) {
            inventoryPage.addProductToCart(i);
        }
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        Assertions.assertEquals(3, cartPage.getCartItemCount());

        inventoryPage.openHamburgerMenu();
        inventoryPage.resetAppState();
        inventoryPage.closeHamburgerMenu();

        // Reload so the cart page re-reads the stored state
        Page.NavigateOptions navigateOptions1 = new Page.NavigateOptions()
                .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED);
        page.navigate("cart.html", navigateOptions1);
        Assertions.assertEquals(0, cartPage.getCartItemCount());

        // The catalogue itself is untouched
        // On GitHub Pages products are in localStorage, resetAppState clears it.
        // Re-populate and reload.
        Page.NavigateOptions navigateOptions2 = new Page.NavigateOptions()
                .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED);
        page.navigate("inventory.html", navigateOptions2);
        String productData = "[" +
                "{\"id\":\"sauce_labs_backpack\",\"name\":\"Sauce Labs Backpack\",\"price\":29.99,\"description\":\"carry.allTheThings()\"}," +
                "{\"id\":\"sauce_labs_bike_light\",\"name\":\"Sauce Labs Bike Light\",\"price\":9.99,\"description\":\"Water-resistant\"}," +
                "{\"id\":\"sauce_labs_fleece_jacket\",\"name\":\"Sauce Labs Fleece Jacket\",\"price\":49.99,\"description\":\"Stylish fleece\"}," +
                "{\"id\":\"sauce_labs_light_bike\",\"name\":\"Sauce Labs Light Bike\",\"price\":15.99,\"description\":\"Lightweight\"}," +
                "{\"id\":\"sauce_labs_bolt_tee\",\"name\":\"Sauce Labs Bolt T-Shirt\",\"price\":15.99,\"description\":\"Full-sleeve\"}," +
                "{\"id\":\"sauce_labs_fast_bike\",\"name\":\"Sauce Labs Fast Bike\",\"price\":53.99,\"description\":\"Racing bicycle\"}" +
                "]";
        for (String user : TestConfig.TEST_USERS) {
            page.evaluate(
                "(data) => { const [products, u] = data; localStorage.setItem(`saucedemo_custom_user_${u}`, products); }",
                new Object[]{productData, user}
            );
        }
        page.reload(new Page.ReloadOptions().setWaitUntil(
                com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED));
        Assertions.assertEquals(6, inventoryPage.getProducts().count());
        Assertions.assertEquals(0, inventoryPage.getCartBadgeCountLocator().count());
    }
}
