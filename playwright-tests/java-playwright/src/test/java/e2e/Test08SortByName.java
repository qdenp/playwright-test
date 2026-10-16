package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

import java.util.List;

/**
 * Sorting products by name (a to z)
 */
class Test08SortByName extends BaseTest {

    @Test
    void sortingProductsByNameAtoZ(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        inventoryPage.sortByAlphaAtoZ();

        // The list has fully rendered - it holds 6 products
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProductNames().count());

        List<String> names = inventoryPage.getAllProductNames();

        for (int i = 1; i < names.size(); i++) {
            Assertions.assertTrue(names.get(i).toLowerCase().compareTo(names.get(i - 1).toLowerCase()) > 0);
        }
    }
}
