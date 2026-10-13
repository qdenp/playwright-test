package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.InventoryPage;

/**
 * Verify quantity stays at 1 per item (SauceDemo doesn't allow editing quantity).
 */
public class QuantityRaceConditionTest extends BaseTest {

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
        cartPage.waitForItemQuantities(java.util.List.of("1", "1", "1"));
        assertEquals(java.util.List.of("1", "1", "1"), cartPage.getItemQuantities());

        // There is no quantity input to edit it with.
        cartPage.waitForQuantityInputCount(0);
        assertEquals(0, cartPage.getQuantityInputCount(), "SauceDemo cannot edit a quantity");
    }

    @Test
    @DisplayName("Adding every product when the add buttons are clicked concurrently")
    void addingEverythingWhenButtonsAreClickedConcurrently() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        int buttonCount = inventoryPage.getAddToCartButtonCount();
        assertEquals(6, buttonCount, "the catalogue offers six add-to-cart buttons");

        // Click all add-to-cart buttons in the same tick, without awaiting each one.
        inventoryPage.addAllProductsToCartConcurrently();

        // All of them must have landed in the cart.
        inventoryPage.waitForCartBadgeCount(buttonCount);

        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(buttonCount);

        // And none of them can have a quantity other than 1.
        var ones = new java.util.ArrayList<String>();
        for (int i = 0; i < buttonCount; i++) {
            ones.add("1");
        }
        cartPage.waitForItemQuantities(ones);
        assertEquals(ones, cartPage.getItemQuantities());
    }
}
