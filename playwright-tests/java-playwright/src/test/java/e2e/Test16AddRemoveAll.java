package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.CartPage;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Adding and removing all products
 */
class Test16AddRemoveAll extends BaseTest {

    @Test
    void addingAndRemovingAllProducts(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProducts().count());

        // Add all products
        for (int i = 0; i < TestConfig.EXPECTED_PRODUCT_COUNT; i++) {
            inventoryPage.addProductToCart(i);
        }

        Assertions.assertEquals(String.valueOf(TestConfig.EXPECTED_PRODUCT_COUNT),
                inventoryPage.getCartBadgeCountLocator().textContent());

        // Go to the cart
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, cartPage.getCartItemCount());

        // Remove all products
        for (int i = 0; i < TestConfig.EXPECTED_PRODUCT_COUNT; i++) {
            cartPage.removeItem(0);
            Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT - 1 - i, cartPage.getCartItemCount());
        }

        // The cart must be empty
        Assertions.assertEquals(0, cartPage.getCartItemCount());

        // Reload in order to verify the badge
        authenticatedPage.page.reload(new Page.ReloadOptions().setWaitUntil(
                com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED));
        Assertions.assertEquals(0, inventoryPage.getCartBadgeCountLocator().count());
    }
}
