package e2e;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.InventoryPage;

/**
 * Product images on the inventory page.
 */
public class ProductImagesTest extends BaseTest {

    @Test
    @DisplayName("All product images are rendered with a source")
    void allProductImagesAreRendered() {
        InventoryPage inventoryPage = loginAsStandardUser();

        // There are exactly as many images as products and every one is visible.
        inventoryPage.waitForProductImageCount(TestConfig.EXPECTED_PRODUCT_COUNT);

        for (int i = 0; i < TestConfig.EXPECTED_PRODUCT_COUNT; i++) {
            assertTrue(inventoryPage.isProductImageVisible(i), "image #" + i + " is visible");
        }

        // Every image has a non-empty src attribute.
        for (int i = 0; i < TestConfig.EXPECTED_PRODUCT_COUNT; i++) {
            String src = inventoryPage.getProductImageSrc(i);
            assertFalse(src == null || src.isEmpty(), "image #" + i + " has a src attribute");
        }
    }
}
