package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.InventoryPage;

/**
 * Performance budget tests.
 *
 * The budgets mirror the reference suites; they are generous enough to stay
 * meaningful on a shared CI machine while still catching a broken page.
 * {@link TestConfig#BUDGET_SCALE} widens them on a starved machine.
 */
public class PerformanceBudgetTest extends BaseTest {

    /** Login page budget, in milliseconds. */
    private static final long LOGIN_PAGE_BUDGET = 3000;
    /** Inventory page budget after login, in milliseconds. */
    private static final long INVENTORY_BUDGET = 3000;
    /** Budget for the whole catalogue, in milliseconds. */
    private static final long CATALOGUE_BUDGET = 5000;
    /** Cart page budget, in milliseconds. */
    private static final long CART_BUDGET = 4000;

    private static final int RUNS = 3;

    @Test
    @DisplayName("The login page loads within the budget")
    void loginPageLoadsWithinBudget() {
        long start = System.nanoTime();
        openPath("/");
        long loadTime = elapsedMillis(start);

        assertTrue(loadTime < TestConfig.budget(LOGIN_PAGE_BUDGET), "the login page loaded in " + loadTime + " ms");
    }

    @Test
    @DisplayName("The inventory page loads within the budget after login")
    void inventoryPageLoadsWithinBudget() {
        long start = System.nanoTime();
        InventoryPage inventoryPage = loginAsStandardUser();
        long loadTime = elapsedMillis(start);

        assertTrue(inventoryPage.isFirstProductVisible(), "the catalogue is rendered");
        assertTrue(loadTime < TestConfig.budget(INVENTORY_BUDGET), "the inventory page loaded in " + loadTime + " ms");
    }

    @Test
    @DisplayName("The whole catalogue loads within the budget")
    void wholeCatalogueLoadsWithinBudget() {
        long start = System.nanoTime();
        InventoryPage inventoryPage = loginAsStandardUser();
        long loadTime = elapsedMillis(start);

        inventoryPage.waitForProductCount(TestConfig.EXPECTED_PRODUCT_COUNT);
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProductCount());
        assertTrue(loadTime < TestConfig.budget(CATALOGUE_BUDGET), "the catalogue loaded in " + loadTime + " ms");
    }

    @Test
    @DisplayName("The cart page loads within the budget")
    void cartPageLoadsWithinBudget() {
        long start = System.nanoTime();
        InventoryPage inventoryPage = loginAsStandardUser();
        inventoryPage.addProductToCart(0);
        inventoryPage.openCart();
        var cartPage = new pageobjects.CartPage(driver);
        cartPage.waitForPageLoad();
        long loadTime = elapsedMillis(start);

        assertTrue(loadTime < TestConfig.budget(CART_BUDGET), "the cart loaded in " + loadTime + " ms");
    }

    @Test
    @DisplayName("Repeated logins keep a stable load time")
    void loadTimeStaysConsistent() {
        long total = 0;
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            InventoryPage inventoryPage = loginAsStandardUser();
            assertTrue(inventoryPage.isFirstProductVisible(), "run #" + (i + 1) + " rendered the catalogue");
            total += elapsedMillis(start);
        }

        long average = total / RUNS;
        assertTrue(average < TestConfig.budget(CATALOGUE_BUDGET), "the average load time is " + average + " ms");
    }

    @Test
    @DisplayName("The product images load within the budget")
    void productImagesLoadWithinBudget() {
        InventoryPage inventoryPage = loginAsStandardUser();

        long start = System.nanoTime();
        inventoryPage.waitForFirstProductImageVisible();
        long loadTime = elapsedMillis(start);

        assertTrue(loadTime < TestConfig.budget(INVENTORY_BUDGET), "the images loaded in " + loadTime + " ms");
    }

    private long elapsedMillis(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}
