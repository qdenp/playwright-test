package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.InventoryPage;

/**
 * Adding and removing all products.
 */
public class Test16AddRemoveAll extends BaseTest {

    @Test
    @DisplayName("Adding and removing all products leaves an empty cart")
    void addingAndRemovingAllProducts() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        int count = inventoryPage.getProductCount();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count);

        // Add all products.
        for (int i = 0; i < TestConfig.EXPECTED_PRODUCT_COUNT; i++) {
            inventoryPage.addProductToCart(i);
        }
        inventoryPage.waitForCartBadgeCount(TestConfig.EXPECTED_PRODUCT_COUNT);

        // Go to the cart.
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(TestConfig.EXPECTED_PRODUCT_COUNT);

        // Remove all products one by one.
        for (int i = 0; i < TestConfig.EXPECTED_PRODUCT_COUNT; i++) {
            cartPage.removeItem(0);
            cartPage.waitForCartItemCount(TestConfig.EXPECTED_PRODUCT_COUNT - 1 - i);
        }

        // The cart must be empty.
        cartPage.waitForCartItemCount(0);
        assertEquals(0, cartPage.getCartItemCount(), "the cart is empty");

        // Reload in order to verify the badge.
        driver.navigate().refresh();
        cartPage.waitForPageLoad();
        inventoryPage.waitForCartBadgeAbsent();
        assertEquals(0, inventoryPage.getCartBadgeCount(), "the badge is gone as well");
    }
}
