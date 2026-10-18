package e2e;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import config.TestConfig;
import pageobjects.CartPage;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Performance budget tests.
 *
 * Measurements use warmup + averaging to avoid cold-start noise (AGENTS.md §3.2).
 *
 * Inventory-page measurements use re-login (not page.reload()) because the
 * test site is a static SPA on GitHub Pages: /inventory.html triggers a 404
 * fallback that redirects to / (login). The auth cookie survives, but
 * page.reload() lands on the login page instead of the inventory page.
 */
class Test27PerformanceBudget extends BaseTest {

    private static final int MEASUREMENT_COUNT = 3;

    private long nowMillis() {
        return System.currentTimeMillis();
    }

    @Test
    void shouldLoadLoginPageWithin2Seconds(Page page) {
        // Warmup — prime caches
        page.navigate(TestConfig.BASE_URL, new Page.NavigateOptions()
                .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        long[] loadTimes = new long[MEASUREMENT_COUNT];
        for (int i = 0; i < MEASUREMENT_COUNT; i++) {
            long startTime = nowMillis();
            page.navigate(TestConfig.BASE_URL, new Page.NavigateOptions()
                    .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED));
            page.waitForLoadState(LoadState.NETWORKIDLE);
            loadTimes[i] = nowMillis() - startTime;
        }

        double avgLoadTime = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3.0;
        System.out.println("Login page avg load time: " + avgLoadTime + "ms over " + MEASUREMENT_COUNT + " runs");
        Assertions.assertTrue(avgLoadTime < TestConfig.LOGIN_PAGE_BUDGET);
    }

    @Test
    void shouldLoadInventoryPageWithin3SecondsAfterLogin(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        LoginPage loginPage = authenticatedPage.loginPage;
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        // Warmup — re-login once to prime caches
        loginPage.goTo();
        loginPage.login();
        inventoryPage.waitForPageLoad();

        long[] loadTimes = new long[MEASUREMENT_COUNT];
        for (int i = 0; i < MEASUREMENT_COUNT; i++) {
            long startTime = nowMillis();
            loginPage.goTo();
            loginPage.login();
            inventoryPage.waitForPageLoad();
            loadTimes[i] = nowMillis() - startTime;
        }

        double avgLoadTime = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3.0;
        System.out.println("Inventory page avg load time: " + avgLoadTime + "ms over " + MEASUREMENT_COUNT + " runs");
        Assertions.assertTrue(avgLoadTime < TestConfig.INVENTORY_PAGE_BUDGET);
    }

    @Test
    void shouldLoadAll6ProductsWithinPerformanceBudget(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        LoginPage loginPage = authenticatedPage.loginPage;
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        // Warmup
        loginPage.goTo();
        loginPage.login();
        inventoryPage.waitForPageLoad();

        long[] loadTimes = new long[MEASUREMENT_COUNT];
        for (int i = 0; i < MEASUREMENT_COUNT; i++) {
            long startTime = nowMillis();
            loginPage.goTo();
            loginPage.login();
            inventoryPage.waitForPageLoad();
            loadTimes[i] = nowMillis() - startTime;
        }

        double avgLoadTime = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3.0;
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProducts().count());
        System.out.println("All 6 products avg load time: " + avgLoadTime + "ms over " + MEASUREMENT_COUNT + " runs");
        Assertions.assertTrue(avgLoadTime < TestConfig.PRODUCTS_LIST_BUDGET);
    }

    @Test
    void shouldLoadCartPageWithin2Seconds(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        // Warmup — add product, open cart, return to inventory
        inventoryPage.addProductToCart(0);
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.continueShopping();
        inventoryPage.waitForPageLoad();

        long[] loadTimes = new long[MEASUREMENT_COUNT];
        for (int i = 0; i < MEASUREMENT_COUNT; i++) {
            long startTime = nowMillis();
            inventoryPage.openCart();
            cartPage.waitForPageLoad();
            loadTimes[i] = nowMillis() - startTime;
            // Navigate back to inventory for the next iteration
            cartPage.continueShopping();
            inventoryPage.waitForPageLoad();
        }

        double avgLoadTime = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3.0;
        System.out.println("Cart page avg load time: " + avgLoadTime + "ms over " + MEASUREMENT_COUNT + " runs");
        Assertions.assertTrue(avgLoadTime < TestConfig.CART_PAGE_BUDGET);
    }

    @Test
    void shouldVerifyPageLoadConsistency3Runs(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        LoginPage loginPage = authenticatedPage.loginPage;
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        // Warmup
        loginPage.goTo();
        loginPage.login();
        inventoryPage.waitForPageLoad();

        long[] loadTimes = new long[MEASUREMENT_COUNT];
        for (int i = 0; i < MEASUREMENT_COUNT; i++) {
            long startTime = nowMillis();
            loginPage.goTo();
            loginPage.login();
            inventoryPage.waitForPageLoad();
            loadTimes[i] = nowMillis() - startTime;
        }

        double avgLoadTime = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3.0;
        System.out.println("Average load time: " + avgLoadTime + "ms");
        Assertions.assertTrue(avgLoadTime < TestConfig.AVERAGE_LOAD_BUDGET);
    }

    @Test
    void shouldVerifyProductImagesLoadWithinBudget(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        LoginPage loginPage = authenticatedPage.loginPage;
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        // Warmup
        loginPage.goTo();
        loginPage.login();
        inventoryPage.waitForPageLoad();
        inventoryPage.getProductImages().first().waitFor(new Locator.WaitForOptions()
                .setTimeout(TestConfig.DEFAULT_TIMEOUT));

        long[] loadTimes = new long[MEASUREMENT_COUNT];
        for (int i = 0; i < MEASUREMENT_COUNT; i++) {
            long startTime = nowMillis();
            loginPage.goTo();
            loginPage.login();
            inventoryPage.waitForPageLoad();
            inventoryPage.getProductImages().first().waitFor(new Locator.WaitForOptions()
                    .setTimeout(TestConfig.DEFAULT_TIMEOUT));
            loadTimes[i] = nowMillis() - startTime;
        }

        double avgLoadTime = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3.0;
        System.out.println("Images avg load time: " + avgLoadTime + "ms over " + MEASUREMENT_COUNT + " runs");
        Assertions.assertTrue(avgLoadTime < TestConfig.IMAGES_BUDGET);
    }
}
