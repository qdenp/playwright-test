package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * should display all product images
 */
class Test11ProductImages extends BaseTest {

    @Test
    void shouldDisplayAllProductImages(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        // Verify there are exactly 6 product images and every one of them is visible
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProductImages().count());

        // Wait for images to be visible (lazy loading may delay rendering)
        for (int i = 0; i < TestConfig.EXPECTED_PRODUCT_COUNT; i++) {
            inventoryPage.getProductImages().nth(i).waitFor(new Locator.WaitForOptions()
                    .setTimeout(TestConfig.DEFAULT_TIMEOUT));
            Assertions.assertTrue(inventoryPage.getProductImages().nth(i).isVisible());
        }

        // Verify each image has a non-empty src attribute
        for (int i = 0; i < TestConfig.EXPECTED_PRODUCT_COUNT; i++) {
            String src = inventoryPage.getProductImages().nth(i).getAttribute("src");
            Assertions.assertNotNull(src);
            Assertions.assertTrue(!src.isEmpty());
        }
    }
}
