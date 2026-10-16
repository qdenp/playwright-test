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
 * Product images on the inventory page.
 */
public class Test11ProductImages extends BaseTest {

    @Test
    @DisplayName("All product images are rendered with a source")
    void allProductImagesAreRendered() {
        InventoryPage inventoryPage = loginAsStandardUser();

        int count = inventoryPage.getProductCount();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count);

        for (int i = 0; i < count; i++) {
            var product = inventoryPage.getProductElement(i);
            var img = product.findElement(inventoryPage.productImages);
            assertTrue(img.isDisplayed(), "image #" + i + " is visible");
            String src = img.getAttribute("src");
            assertFalse(src == null || src.isEmpty(), "image #" + i + " has a src attribute");
        }
    }
}