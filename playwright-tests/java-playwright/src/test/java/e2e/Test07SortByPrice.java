package e2e;

import com.microsoft.playwright.*;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

import java.util.List;

/**
 * Sorting products by price
 */
class Test07SortByPrice extends BaseTest {

    @Test
    void sortingProductsByPriceLowToHigh(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        // Sort by price low to high
        inventoryPage.sortByPriceLowToHigh();

        // Verify prices are in ascending order
        List<Double> prices = inventoryPage.getAllProductPrices();
        for (int i = 1; i < prices.size(); i++) {
            Assertions.assertTrue(prices.get(i) >= prices.get(i - 1));
        }
        Assertions.assertEquals(Math.min(prices.get(0), prices.get(1)), prices.get(0), 0.01);
    }

    @Test
    void sortingProductsByPriceHighToLow(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        // Sort by price high to low
        inventoryPage.sortByPriceHighToLow();

        // Verify prices are in descending order
        List<Double> prices = inventoryPage.getAllProductPrices();
        for (int i = 1; i < prices.size(); i++) {
            Assertions.assertTrue(prices.get(i) <= prices.get(i - 1));
        }
        Assertions.assertEquals(Math.max(prices.get(0), prices.get(1)), prices.get(0), 0.01);
    }
}
