package e2e;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.InventoryPage;

/**
 * Product cards on the inventory page.
 */
public class ProductCardsTest extends BaseTest {

    @Test
    @DisplayName("Every product card carries a name, a description, a price and a button")
    void everyProductCardIsComplete() {
        InventoryPage inventoryPage = loginAsStandardUser();

        // Each card carries a name, description, price and an add-to-cart button.
        inventoryPage.waitForProductCount(TestConfig.EXPECTED_PRODUCT_COUNT);
        inventoryPage.waitForProductNamesCount(TestConfig.EXPECTED_PRODUCT_COUNT);
        inventoryPage.waitForProductDescriptionCount(TestConfig.EXPECTED_PRODUCT_COUNT);
        inventoryPage.waitForProductPriceCount(TestConfig.EXPECTED_PRODUCT_COUNT);
        inventoryPage.waitForAddToCartButtonCount(TestConfig.EXPECTED_PRODUCT_COUNT);
        inventoryPage.waitForProductImageCount(TestConfig.EXPECTED_PRODUCT_COUNT);

        // All elements are non-empty and visible.
        assertFalse(inventoryPage.getFirstProductName().isEmpty(), "the first card has a name");
        assertFalse(inventoryPage.getFirstProductDescription().isEmpty(), "the first card has a description");
        assertTrue(inventoryPage.isFirstProductPriceFormatted(), "the first card has a price");
        assertTrue(inventoryPage.isFirstAddToCartButtonVisible(), "the first card has an add-to-cart button");
    }
}
