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
 * <p>Measurements use warmup + averaging to avoid cold-start noise
 * (AGENTS.md §3.2). Re-login uses {@code resetToLoginPage()} (cookie deletion
 * plus a navigation to about:blank to invalidate Chrome's bfcache) instead of
 * page.reload() because the test site is a static SPA on GitHub Pages:
 * /inventory.html triggers a 404 fallback that redirects to / (login).
 * page.reload() lands on the login page after cookie deletion because the
 * SPA serves its cached in-memory state. {@code resetToLoginPage()} forces
 * a fresh page load from the server.</p>
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
            "the login page took " + avg + " ms on average");
    }

    @Test
    @DisplayName("The inventory page loads within the budget after login")
    void inventoryPageLoadsWithinBudget() {
        // Warmup — first login (fresh browser from @BeforeEach)
        InventoryPage inventoryPage = loginAsStandardUser();

        long[] loadTimes = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            resetToLoginPage();
            long start = System.nanoTime();
            inventoryPage = loginAsStandardUser();
            loadTimes[i] = elapsedMillis(start);
        }

        assertTrue(inventoryPage.isFirstProductVisible(), "the catalogue is rendered");
        long avg = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3;
        assertTrue(avg < TestConfig.budget(TestConfig.INVENTORY_BUDGET),
            "the inventory page took " + avg + " ms on average");
    }

    @Test
    @DisplayName("The whole catalogue loads within the budget")
    void wholeCatalogueLoadsWithinBudget() {
        // Warmup
        InventoryPage inventoryPage = loginAsStandardUser();

        long[] loadTimes = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            resetToLoginPage();
            long start = System.nanoTime();
            inventoryPage = loginAsStandardUser();
            loadTimes[i] = elapsedMillis(start);
        }

        int count = inventoryPage.getProductCount();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count);
        long avg = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3;
        assertTrue(avg < TestConfig.budget(TestConfig.CATALOGUE_BUDGET),
            "the catalogue took " + avg + " ms on average");
    }

    @Test
    @DisplayName("The cart page loads within the budget")
    void cartPageLoadsWithinBudget() {
        // Warmup — login, add product, open cart, return to inventory
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
            "the cart page took " + avg + " ms on average");
    }

    @Test
    @DisplayName("Repeated logins keep a stable load time")
    void loadTimeStaysConsistent() {
        // Warmup — first login (fresh browser)
        InventoryPage inventoryPage = loginAsStandardUser();

        long[] loadTimes = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            resetToLoginPage();
            long start = System.nanoTime();
            inventoryPage = loginAsStandardUser();
            loadTimes[i] = elapsedMillis(start);
        }

        assertTrue(inventoryPage.isFirstProductVisible(), "run #" + RUNS + " rendered the catalogue");
        long avg = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3;
        assertTrue(avg < TestConfig.budget(TestConfig.CATALOGUE_BUDGET),
            "the average load time is " + avg + " ms");
    }

    @Test
    @DisplayName("The product images load within the budget")
    void productImagesLoadWithinBudget() {
        // Warmup
        InventoryPage inventoryPage = loginAsStandardUser();
        wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryPage.productImages));

        long[] loadTimes = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            resetToLoginPage();
            long start = System.nanoTime();
            inventoryPage = loginAsStandardUser();
            wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryPage.productImages));
            loadTimes[i] = elapsedMillis(start);
        }

        int count = inventoryPage.getProductCount();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count);
        long avg = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3;
        assertTrue(avg < TestConfig.budget(TestConfig.INVENTORY_BUDGET),
            "the images took " + avg + " ms on average");
    }

    private long elapsedMillis(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}
