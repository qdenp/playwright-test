package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.InventoryPage;

/**
 * Adding a product to the cart.
 */
public class AddToCartTest extends BaseTest {

    @Test
    @DisplayName("Adding products to the cart keeps the badge and the cart in sync")
    void addingProductsToCart() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        // Add the first product - badge shows 1.
        inventoryPage.addProductToCart(0);
        inventoryPage.waitForCartBadgeCount(1);

        // Open the cart and verify the first product.
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(1);
        var firstCartNames = cartPage.getCartItemNames();
        assertEquals(1, firstCartNames.size());
        assertFalse(firstCartNames.get(0).isEmpty(), "the cart shows the product name");

        // Return to the inventory page.
        cartPage.continueShopping();
        inventoryPage.waitForPageLoad();

        // Add a second product - badge shows 2.
        inventoryPage.addProductToCart(1);
        inventoryPage.waitForCartBadgeCount(2);

        // Open the cart and verify both products.
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(2);

        var bothCartNames = cartPage.getCartItemNames();
        assertEquals(2, bothCartNames.size());
        assertFalse(bothCartNames.get(0).isEmpty());
        assertFalse(bothCartNames.get(1).isEmpty());
        // The products must be different.
        assertNotEquals(bothCartNames.get(0), bothCartNames.get(1));
        assertTrue(driver.getCurrentUrl().contains("cart.html"), "the cart page is open");
    }
}
