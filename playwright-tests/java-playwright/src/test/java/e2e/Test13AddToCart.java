package e2e;

import com.microsoft.playwright.*;
import pageobjects.CartPage;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Adding a product to the cart
 */
class Test13AddToCart extends BaseTest {

    @Test
    void addingAProductToTheCart(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        // Add the first product - badge shows 1
        inventoryPage.addProductToCart(0);
        Assertions.assertEquals("1", inventoryPage.getCartBadgeCountLocator().textContent());

        // Open the cart and verify the first product
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        Assertions.assertEquals(1, cartPage.getCartItemCount());

        java.util.List<String> firstCartNames = cartPage.getCartItemNames();
        Assertions.assertTrue(!firstCartNames.get(0).isEmpty());
        Assertions.assertTrue(firstCartNames.get(0).length() > 0);

        // Return to the inventory page
        cartPage.continueShopping();
        inventoryPage.waitForPageLoad();

        // Add a second product - badge shows 2
        inventoryPage.addProductToCart(1);
        Assertions.assertEquals("2", inventoryPage.getCartBadgeCountLocator().textContent());

        // Open the cart and verify both products
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        Assertions.assertEquals(2, cartPage.getCartItemCount());

        java.util.List<String> bothCartNames = cartPage.getCartItemNames();
        Assertions.assertTrue(!bothCartNames.get(1).isEmpty());
        Assertions.assertTrue(bothCartNames.get(1).length() > 0);
        // The products must be different
        Assertions.assertNotEquals(bothCartNames.get(0), bothCartNames.get(1));
    }
}
