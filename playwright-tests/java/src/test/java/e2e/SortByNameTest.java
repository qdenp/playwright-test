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
 * Sorting products by name.
 */
public class SortByNameTest extends BaseTest {

    @Test
    @DisplayName("Sorting products by name from A to Z")
    void sortByNameFromAToZ() {
        InventoryPage inventoryPage = loginAsStandardUser();

        inventoryPage.sortByNameAtoZ();
        // The list has fully rendered - it holds the whole catalogue.
        inventoryPage.waitForProductNamesCount(TestConfig.EXPECTED_PRODUCT_COUNT);

        List<String> names = inventoryPage.getAllProductNames();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, names.size());

        for (int i = 1; i < names.size(); i++) {
            assertTrue(names.get(i).toLowerCase().compareTo(names.get(i - 1).toLowerCase()) > 0,
                "\"" + names.get(i) + "\" must come after \"" + names.get(i - 1) + "\"");
        }
    }
}
