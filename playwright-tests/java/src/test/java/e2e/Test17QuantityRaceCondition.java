package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.InventoryPage;

/**
 * Verify quantity stays at 1 per item (SauceDemo doesn't allow editing quantity).
 */
public class Test17QuantityRaceCondition extends BaseTest {

    @Test
    @DisplayName("Every cart item shows quantity 1 and offers no way to edit it")
    void quantityStaysAtOnePerItem() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(2);
        inventoryPage.addProductToCart(4);
        inventoryPage.waitForCartBadgeCount(3);

        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        // Every item is displayed with quantity 1.
        cartPage.waitForCartItemCount(3);
        var quantities = cartPage.getCartItemQuantities();
        assertEquals(3, quantities.size());
        for (int q : quantities) {
            assertEquals(1, q, "every cart item shows quantity 1");
        }
    }

    @Test
    @DisplayName("Adding multiple products in sequence")
    void addingMultipleProductsInSequence() {
        InventoryPage inventoryPage = loginAsStandardUser();

        // Add all products sequentially.
        int count = inventoryPage.getProductCount();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count, "the catalogue offers six add-to-cart buttons");

        for (int i = 0; i < count; i++) {
            inventoryPage.addProductToCart(i);
        }
        inventoryPage.waitForCartBadgeCount(count);

        // All of them must have landed in the cart.
        CartPage cartPage = new CartPage(driver);
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(count);

        // And none of them can have a quantity other than 1.
        var quantities = cartPage.getCartItemQuantities();
        for (int q : quantities) {
            assertEquals(1, q, "every cart item shows quantity 1");
        }
    }
}