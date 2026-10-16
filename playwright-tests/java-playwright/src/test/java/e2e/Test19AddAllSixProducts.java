package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.CartPage;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

import java.util.List;

/**
 * Add all 6 products and verify
 */
class Test19AddAllSixProducts extends BaseTest {

    @Test
    void shouldAddAll6ProductsAndVerifyCart(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        // Add all 6 products (indices 0-5)
        for (int i = 0; i < TestConfig.EXPECTED_PRODUCT_COUNT; i++) {
            inventoryPage.addProductToCart(i);
        }

        // Verify cart badge shows 6
        Assertions.assertEquals(String.valueOf(TestConfig.EXPECTED_PRODUCT_COUNT),
                inventoryPage.getCartBadgeCountLocator().textContent());

        // Open cart
        inventoryPage.openCart();

        // Verify all 6 items in cart
        cartPage.waitForPageLoad();
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, cartPage.getCartItemCount());
    }

    @Test
    void shouldVerifyProductCountAndPrices(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        // Verify 6 products loaded
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProducts().count());

        // Verify all prices are valid numbers > 0
        List<Double> prices = inventoryPage.getAllProductPrices();
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, prices.size());
        for (Double price : prices) {
            Assertions.assertTrue(price > 0);
            Assertions.assertTrue(price <= 100);
        }
    }

    @Test
    void shouldAddAll6ProductsAndVerifyProductNames(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        // Add all 6 products
        for (int i = 0; i < TestConfig.EXPECTED_PRODUCT_COUNT; i++) {
            inventoryPage.addProductToCart(i);
        }

        // Open cart
        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        // Wait for the products to render before reading the list
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, cartPage.getCartItemCount());

        // Verify specific product names
        List<String> expectedNames = List.of(
                "Sauce Labs Backpack",
                "Sauce Labs Bike Light",
                "Sauce Labs Bolt T-Shirt",
                "Sauce Labs Fleece Jacket",
                "Sauce Labs Onesie",
                "Test.allTheThings() T-Shirt (Red)"
        );

        List<String> cartItems = cartPage.getCartItemNames();
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, cartItems.size());

        for (String name : expectedNames) {
            Assertions.assertTrue(cartItems.contains(name));
        }
    }
}
