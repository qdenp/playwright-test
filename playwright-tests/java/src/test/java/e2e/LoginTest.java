package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.InventoryPage;

/**
 * Successful login with the standard user.
 */
public class LoginTest extends BaseTest {

    @Test
    @DisplayName("Successful login with the standard user")
    void successfulLoginWithStandardUser() {
        InventoryPage inventoryPage = loginAsStandardUser();

        assertTrue(driver.getCurrentUrl().contains("inventory.html"),
            "a successful login lands on the inventory page");
        assertEquals("Products", inventoryPage.getTitleText());
        assertTrue(inventoryPage.isFirstProductVisible(), "the catalogue is rendered");
    }
}
