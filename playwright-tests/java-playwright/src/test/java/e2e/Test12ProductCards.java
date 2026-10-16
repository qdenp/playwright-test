package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Product cards on the inventory page
 */
class Test12ProductCards extends BaseTest {

    @Test
    void productCardsOnTheInventoryPage(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        // Each card carries a name, description, price and an add-to-cart button
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProducts().count());
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProductNames().count());
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProductDescriptions().count());
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProductPrices().count());
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getAddToCartButtons().count());

        // All elements are non-empty and visible
        Assertions.assertNotNull(inventoryPage.getProductNames().first().textContent());
        Assertions.assertNotNull(inventoryPage.getProductDescriptions().first().textContent());
        Assertions.assertTrue(inventoryPage.getProductPrices().first().textContent().contains("$"));
        Assertions.assertTrue(inventoryPage.getAddToCartButtons().first().isVisible());
    }
}
