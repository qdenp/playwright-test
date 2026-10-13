package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.InventoryPage;

/**
 * Sorting products by price.
 */
public class SortByPriceTest extends BaseTest {

    @Test
    @DisplayName("Sorting products by price from low to high")
    void sortByPriceLowToHigh() {
        InventoryPage inventoryPage = loginAsStandardUser();

        inventoryPage.sortByPriceLowToHigh();
        inventoryPage.waitForProductCount(TestConfig.EXPECTED_PRODUCT_COUNT);

        List<Double> prices = inventoryPage.getAllProductPrices();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, prices.size());

        for (int i = 1; i < prices.size(); i++) {
            assertTrue(prices.get(i) >= prices.get(i - 1),
                "price #" + i + " (" + InventoryPage.money(prices.get(i))
                    + ") must not be lower than price #" + (i - 1)
                    + " (" + InventoryPage.money(prices.get(i - 1)) + ")");
        }
        assertEquals(prices.stream().mapToDouble(Double::doubleValue).min().orElseThrow(),
            prices.get(0), "the cheapest product comes first");
    }

    @Test
    @DisplayName("Sorting products by price from high to low")
    void sortByPriceHighToLow() {
        InventoryPage inventoryPage = loginAsStandardUser();

        inventoryPage.sortByPriceHighToLow();
        inventoryPage.waitForProductCount(TestConfig.EXPECTED_PRODUCT_COUNT);

        List<Double> prices = inventoryPage.getAllProductPrices();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, prices.size());

        for (int i = 1; i < prices.size(); i++) {
            assertTrue(prices.get(i) <= prices.get(i - 1),
                "price #" + i + " (" + InventoryPage.money(prices.get(i))
                    + ") must not be higher than price #" + (i - 1)
                    + " (" + InventoryPage.money(prices.get(i - 1)) + ")");
        }
        assertEquals(prices.stream().mapToDouble(Double::doubleValue).max().orElseThrow(),
            prices.get(0), "the most expensive product comes first");
    }
}
