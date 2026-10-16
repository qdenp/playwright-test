package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.InventoryPage;

/**
 * Cart badge increments by 1 on add and decrements by 1 on remove.
 */
public class Test15CartQuantity extends BaseTest {

    @Test
    @DisplayName("Cart badge increments on add and decrements on remove")
    void cartBadgeFollowsTheCart() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        // The cart starts empty - no badge.
        inventoryPage.waitForCartBadgeAbsent();
        assertEquals(0, inventoryPage.getCartBadgeCount(), "an empty cart has no badge");

        // Adding a product increments the badge by 1.
        inventoryPage.addProductToCart(0);
        inventoryPage.waitForCartBadgeCount(1);

        // Adding a second product increments the badge again.
        inventoryPage.addProductToCart(1);
        inventoryPage.waitForCartBadgeCount(2);

        // Going to the cart and removing one product decrements the badge.
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(2);

        cartPage.removeItem(0);
        cartPage.waitForCartItemCount(1);
        inventoryPage.waitForCartBadgeCount(1);

        // Removing the last product clears the badge.
        cartPage.removeItem(0);
        cartPage.waitForCartItemCount(0);
        inventoryPage.waitForCartBadgeAbsent();
        assertEquals(0, inventoryPage.getCartBadgeCount(), "the badge is gone");
    }
}
