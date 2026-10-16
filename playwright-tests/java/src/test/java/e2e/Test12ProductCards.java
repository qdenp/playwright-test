package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class Test12ProductCards extends BaseTest {

    @Test
    @DisplayName("Every product card carries a name, a description, a price and a button")
    void everyProductCardIsComplete() {
        InventoryPage inventoryPage = loginAsStandardUser();

        int count = inventoryPage.getProductCount();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count);

        // All elements are non-empty and visible for the first product.
        assertFalse(inventoryPage.getProductTitle(0).isEmpty(), "the first card has a name");
        assertFalse(inventoryPage.getProductDescription(0).isEmpty(), "the first card has a description");
        double price = inventoryPage.getProductPrice(0);
        assertTrue(price > 0, "the first card has a price: " + price);
        var product = inventoryPage.getProductElement(0);
        var btn = product.findElement(inventoryPage.addToCartButtons);
        assertTrue(btn.isDisplayed(), "the first card has an add-to-cart button");
    }
}