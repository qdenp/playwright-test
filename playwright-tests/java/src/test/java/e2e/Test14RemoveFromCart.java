package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.InventoryPage;

/**
 * Removing a product from the cart.
 */
public class Test14RemoveFromCart extends BaseTest {

    @Test
    @DisplayName("Removing a product from the cart survives a reload")
    void removingAProductFromCart() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(1);
        inventoryPage.waitForCartBadgeCount(2);

        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(2);

        cartPage.removeItem(0);
        cartPage.waitForCartItemCount(1);

        // Go back to inventory and verify badge reflects the removal
        cartPage.continueShopping();
        inventoryPage.waitForPageLoad();
        inventoryPage.waitForCartBadgeCount(1);
        assertEquals(1, inventoryPage.getCartBadgeCount(), "the badge reflects the stored cart");
    }
}
