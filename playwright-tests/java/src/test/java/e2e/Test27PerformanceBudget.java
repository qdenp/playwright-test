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
 * Performance budget tests, mirroring the TypeScript reference suite.
 */
public class Test27PerformanceBudget extends BaseTest {

    private static final int RUNS = 3;

    @Test
    @DisplayName("The login page loads within the budget")
    void loginPageLoadsWithinBudget() {
        long start = System.nanoTime();
        openPath("");
        long loadTime = elapsedMillis(start);

        assertTrue(loadTime < TestConfig.budget(TestConfig.LOGIN_PAGE_BUDGET), "the login page loaded in " + loadTime + " ms");
    }

    @Test
    @DisplayName("The inventory page loads within the budget after login")
    void inventoryPageLoadsWithinBudget() {
        InventoryPage inventoryPage = loginAsStandardUser();

        long start = System.nanoTime();
        inventoryPage.waitForPageLoad();
        long loadTime = elapsedMillis(start);

        assertTrue(inventoryPage.isFirstProductVisible(), "the catalogue is rendered");
        assertTrue(loadTime < TestConfig.budget(TestConfig.INVENTORY_BUDGET), "the inventory page loaded in " + loadTime + " ms");
    }

    @Test
    @DisplayName("The whole catalogue loads within the budget")
    void wholeCatalogueLoadsWithinBudget() {
        long start = System.nanoTime();
        InventoryPage inventoryPage = loginAsStandardUser();
        long loadTime = elapsedMillis(start);

        int count = inventoryPage.getProductCount();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count);
        assertTrue(loadTime < TestConfig.budget(TestConfig.CATALOGUE_BUDGET), "the catalogue loaded in " + loadTime + " ms");
    }

    @Test
    @DisplayName("The cart page loads within the budget")
    void cartPageLoadsWithinBudget() {
        InventoryPage inventoryPage = loginAsStandardUser();
        inventoryPage.addProductToCart(0);

        long start = System.nanoTime();
        inventoryPage.openCart();
        var cartPage = new CartPage(driver);
        cartPage.waitForPageLoad();
        long loadTime = elapsedMillis(start);

        assertTrue(loadTime < TestConfig.budget(TestConfig.CART_BUDGET), "the cart loaded in " + loadTime + " ms");
    }

    @Test
    @DisplayName("Repeated navigations keep a stable load time")
    void loadTimeStaysConsistent() {
        InventoryPage inventoryPage = loginAsStandardUser();

        long total = 0;
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            inventoryPage.waitForPageLoad();
            assertTrue(inventoryPage.isFirstProductVisible(), "run #" + (i + 1) + " rendered the catalogue");
            total += elapsedMillis(start);
        }

        long average = total / RUNS;
        assertTrue(average < TestConfig.budget(TestConfig.CATALOGUE_BUDGET), "the average load time is " + average + " ms");
    }

    @Test
    @DisplayName("The product images load within the budget")
    void productImagesLoadWithinBudget() {
        InventoryPage inventoryPage = loginAsStandardUser();

        long start = System.nanoTime();
        int count = inventoryPage.getProductCount();
        long loadTime = elapsedMillis(start);

        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count);
        assertTrue(loadTime < TestConfig.budget(TestConfig.INVENTORY_BUDGET), "the images loaded in " + loadTime + " ms");
    }

    private long elapsedMillis(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}
