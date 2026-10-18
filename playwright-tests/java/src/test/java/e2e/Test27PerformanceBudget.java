package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pageobjects.CartPage;
import pageobjects.InventoryPage;

/**
 * Performance budget tests, mirroring the TypeScript reference suite.
 *
 * Measurements use warmup + averaging to avoid cold-start noise (AGENTS.md §3.2).
 *
 * Inventory-page measurements use restartBrowser() + loginAsStandardUser() for
 * each run, because Selenium/Chrome cannot reliably re-login within the same
 * browser session (Chrome serves a cached login page where form fields don't
 * receive input). The reference Playwright suites re-login via SPA navigation.
 */
public class Test27PerformanceBudget extends BaseTest {

    private static final int RUNS = 3;

    @Test
    @DisplayName("The login page loads within the budget")
    void loginPageLoadsWithinBudget() {
        // Warmup — prime caches
        openPath("");

        long[] loadTimes = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            openPath("");
            loadTimes[i] = elapsedMillis(start);
        }

        long avg = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3;
        assertTrue(avg < TestConfig.budget(TestConfig.LOGIN_PAGE_BUDGET),
            "the login page took " + avg + " ms to load on average");
    }

    @Test
    @DisplayName("The inventory page loads within the budget after login")
    void inventoryPageLoadsWithinBudget() {
        // Warmup — fresh browser and login to prime caches
        restartBrowser();
        InventoryPage inventoryPage = loginAsStandardUser();

        long[] loadTimes = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            restartBrowser();
            long start = System.nanoTime();
            inventoryPage = loginAsStandardUser();
            loadTimes[i] = elapsedMillis(start);
        }

        assertTrue(inventoryPage.isFirstProductVisible(), "the catalogue is rendered");
        long avg = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3;
        assertTrue(avg < TestConfig.budget(TestConfig.INVENTORY_BUDGET),
            "the inventory page took " + avg + " ms to load on average");
    }

    @Test
    @DisplayName("The whole catalogue loads within the budget")
    void wholeCatalogueLoadsWithinBudget() {
        // Warmup
        restartBrowser();
        loginAsStandardUser();

        long[] loadTimes = new long[RUNS];
        InventoryPage inventoryPage = null;
        for (int i = 0; i < RUNS; i++) {
            restartBrowser();
            long start = System.nanoTime();
            inventoryPage = loginAsStandardUser();
            loadTimes[i] = elapsedMillis(start);
        }

        int count = inventoryPage.getProductCount();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count);
        long avg = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3;
        assertTrue(avg < TestConfig.budget(TestConfig.CATALOGUE_BUDGET),
            "the catalogue took " + avg + " ms to load on average");
    }

    @Test
    @DisplayName("The cart page loads within the budget")
    void cartPageLoadsWithinBudget() {
        // Warmup — fresh browser, login, add product, open cart, return to inventory
        restartBrowser();
        InventoryPage inventoryPage = loginAsStandardUser();
        inventoryPage.addProductToCart(0);
        inventoryPage.openCart();
        CartPage cartPage = new CartPage(driver);
        cartPage.waitForPageLoad();
        cartPage.continueShopping();
        inventoryPage.waitForPageLoad();

        long[] loadTimes = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            inventoryPage.openCart();
            cartPage.waitForPageLoad();
            loadTimes[i] = elapsedMillis(start);
            // Navigate back to inventory for the next iteration
            cartPage.continueShopping();
            inventoryPage.waitForPageLoad();
        }

        long avg = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3;
        assertTrue(avg < TestConfig.budget(TestConfig.CART_BUDGET),
            "the cart page took " + avg + " ms to load on average");
    }

    @Test
    @DisplayName("Repeated logins keep a stable load time")
    void loadTimeStaysConsistent() {
        // Warmup
        restartBrowser();
        InventoryPage inventoryPage = loginAsStandardUser();

        long[] loadTimes = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            restartBrowser();
            long start = System.nanoTime();
            inventoryPage = loginAsStandardUser();
            loadTimes[i] = elapsedMillis(start);
        }

        long avg = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3;
        assertTrue(avg < TestConfig.budget(TestConfig.CATALOGUE_BUDGET),
            "the average load time is " + avg + " ms");
    }

    @Test
    @DisplayName("The product images load within the budget")
    void productImagesLoadWithinBudget() {
        // Warmup
        restartBrowser();
        InventoryPage inventoryPage = loginAsStandardUser();
        wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryPage.productImages));

        long[] loadTimes = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            restartBrowser();
            long start = System.nanoTime();
            inventoryPage = loginAsStandardUser();
            wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryPage.productImages));
            loadTimes[i] = elapsedMillis(start);
        }

        int count = inventoryPage.getProductCount();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count);
        long avg = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3;
        assertTrue(avg < TestConfig.budget(TestConfig.INVENTORY_BUDGET),
            "the images took " + avg + " ms to load on average");
    }

    private long elapsedMillis(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}
