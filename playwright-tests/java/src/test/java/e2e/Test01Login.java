package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.InventoryPage;

/**
 * Successful login with the standard user.
 */
public class Test01Login extends BaseTest {

    @Test
    @DisplayName("Successful login with the standard user")
    void successfulLoginWithStandardUser() {
        InventoryPage inventoryPage = loginAsStandardUser();

        assertTrue(driver.getCurrentUrl().contains("inventory.html"),
            "a successful login lands on the inventory page");
        assertEquals(TestConfig.INVENTORY_PAGE_TITLE, inventoryPage.getTitleText());
        assertTrue(inventoryPage.isFirstProductVisible(), "the catalogue is rendered");
    }
}
