package e2e;

import com.microsoft.playwright.*;
import pageobjects.CartPage;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Cart badge increments on add and decrements on remove
 */
class Test15CartQuantity extends BaseTest {

    @Test
    void cartBadgeIncrementsOnAddAndDecrementsOnRemove(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        // The cart starts empty - no badge
        Assertions.assertEquals(0, inventoryPage.getCartBadgeCountLocator().count());

        // Adding a product increments the badge by 1
        inventoryPage.addProductToCart(0);
        Assertions.assertEquals("1", inventoryPage.getCartBadgeCountLocator().textContent());

        // Adding a second product increments the badge again
        inventoryPage.addProductToCart(1);
        Assertions.assertEquals("2", inventoryPage.getCartBadgeCountLocator().textContent());

        // Going to the cart and removing one product decrements the badge
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        Assertions.assertEquals(2, cartPage.getCartItemCount());

        cartPage.removeItem(0);
        Assertions.assertEquals(1, cartPage.getCartItemCount());
        Assertions.assertEquals("1", inventoryPage.getCartBadgeCountLocator().textContent());

        // Removing the last product clears the badge
        cartPage.removeItem(0);
        Assertions.assertEquals(0, cartPage.getCartItemCount());
        Assertions.assertEquals(0, inventoryPage.getCartBadgeCountLocator().count());
    }
}
