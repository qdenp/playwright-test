package base;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.junit.UsePlaywright;
import config.TestConfig;
import pageobjects.*;

/**
 * Base test class with Playwright fixtures.
 * Mirrors TypeScript fixtures.ts: provides 'page' and 'authenticatedPage'.
 */
@UsePlaywright(config.PlaywrightConfig.class)
public abstract class BaseTest {

    /**
     * Pre-authenticated page fixture.
     * Tests that need login should declare this parameter.
     */
    public static class AuthenticatedPage {
        public final Page page;
        public final LoginPage loginPage;
        public final InventoryPage inventoryPage;
        public final CartPage cartPage;
        public final CheckoutPage checkoutPage;
        public final ItemDetailPage itemDetailPage;

        public AuthenticatedPage(Page page, LoginPage loginPage, InventoryPage inventoryPage,
                                 CartPage cartPage, CheckoutPage checkoutPage, ItemDetailPage itemDetailPage) {
            this.page = page;
            this.loginPage = loginPage;
            this.inventoryPage = inventoryPage;
            this.cartPage = cartPage;
            this.checkoutPage = checkoutPage;
            this.itemDetailPage = itemDetailPage;
        }
    }

    /**
     * Setup authenticated page fixture.
     * Call from test method with Page parameter.
     */
    public AuthenticatedPage setupAuthenticatedPage(Page page) {
        // Navigate first, then pre-populate localStorage for GitHub Pages
        LoginPage loginPage = new LoginPage(page);
        InventoryPage inventoryPage = new InventoryPage(page);
        CartPage cartPage = new CartPage(page);
        CheckoutPage checkoutPage = new CheckoutPage(page);
        ItemDetailPage itemDetailPage = new ItemDetailPage(page);

        // Navigate to login page
        loginPage.goTo();

        // Pre-populate localStorage for all test users
        String productData = "[" +
                "{\"id\":\"sauce_labs_backpack\",\"name\":\"Sauce Labs Backpack\",\"price\":29.99,\"description\":\"carry.allTheThings()\"}," +
                "{\"id\":\"sauce_labs_bike_light\",\"name\":\"Sauce Labs Bike Light\",\"price\":9.99,\"description\":\"Water-resistant\"}," +
                "{\"id\":\"sauce_labs_fleece_jacket\",\"name\":\"Sauce Labs Fleece Jacket\",\"price\":49.99,\"description\":\"Stylish fleece\"}," +
                "{\"id\":\"sauce_labs_light_bike\",\"name\":\"Sauce Labs Light Bike\",\"price\":15.99,\"description\":\"Lightweight\"}," +
                "{\"id\":\"sauce_labs_bolt_tee\",\"name\":\"Sauce Labs Bolt T-Shirt\",\"price\":15.99,\"description\":\"Full-sleeve\"}," +
                "{\"id\":\"sauce_labs_fast_bike\",\"name\":\"Sauce Labs Fast Bike\",\"price\":53.99,\"description\":\"Racing bicycle\"}" +
                "]";

        String[] users = TestConfig.TEST_USERS;

        // For GitHub Pages: products are served from localStorage
        for (String user : users) {
            page.evaluate(
                "(data) => { const [products, user] = data; localStorage.setItem(`saucedemo_custom_user_${user}`, products); }",
                new Object[]{productData, user}
            );
        }

        // Reload to pick up the localStorage
        page.reload(new Page.ReloadOptions().setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED));

        // Login
        loginPage.login();

        // Wait for inventory page to load
        inventoryPage.waitForPageLoad();

        return new AuthenticatedPage(page, loginPage, inventoryPage, cartPage, checkoutPage, itemDetailPage);
    }
}
