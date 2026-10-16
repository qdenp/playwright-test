package e2e;

import com.microsoft.playwright.*;
import pageobjects.CartPage;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Removing a product from the cart
 */
class Test14RemoveFromCart extends BaseTest {

    @Test
    void removingAProductFromTheCart(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(1);
        Assertions.assertEquals("2", inventoryPage.getCartBadgeCountLocator().textContent());

        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        Assertions.assertEquals(2, cartPage.getCartItemCount());

        cartPage.removeItem(0);
        Assertions.assertEquals(1, cartPage.getCartItemCount());

        // Go back to inventory and verify badge reflects the removal
        cartPage.continueShopping();
        inventoryPage.waitForPageLoad();
        Assertions.assertEquals("1", inventoryPage.getCartBadgeCountLocator().textContent());
    }
}
