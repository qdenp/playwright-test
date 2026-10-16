package e2e;

import com.microsoft.playwright.*;
import pageobjects.CartPage;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Specific item combinations (3 of 6 products)
 */
class Test18ItemCombinations extends BaseTest {

    @Test
    void shouldAddCheapest3ProductsAndVerifyCart(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        // Record the catalog price of every product before picking the cheapest
        List<Double> catalogPrices = inventoryPage.getAllProductPrices();
        List<Double> cheapestThree = catalogPrices.stream()
                .sorted()
                .limit(3)
                .collect(Collectors.toList());

        // Add 3 cheapest: Onesie (7.99), Bike Light (9.99), Red T-Shirt (15.99)
        inventoryPage.addProductToCart(4); // Onesie - $7.99
        inventoryPage.addProductToCart(1); // Bike Light - $9.99
        inventoryPage.addProductToCart(5); // Red T-Shirt - $15.99

        Assertions.assertEquals("3", inventoryPage.getCartBadgeCountLocator().textContent());

        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        Assertions.assertEquals(3, cartPage.getCartItemCount());

        List<String> cartItems = cartPage.getCartItemNames();
        Assertions.assertTrue(cartItems.contains("Sauce Labs Onesie"));
        Assertions.assertTrue(cartItems.contains("Sauce Labs Bike Light"));
        Assertions.assertTrue(cartItems.contains("Test.allTheThings() T-Shirt (Red)"));

        // The three cheapest in the catalogue are the three items in the cart
        List<Double> pricesInCart = cartPage.getCartItemPrices().stream()
                .sorted()
                .collect(Collectors.toList());
        Assertions.assertEquals(cheapestThree, pricesInCart);
    }

    @Test
    void shouldAddMostExpensive3ProductsAndVerifyCart(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        List<Double> catalogPrices = inventoryPage.getAllProductPrices();
        List<Double> priciestThree = catalogPrices.stream()
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .collect(Collectors.toList());

        // Add 3 most expensive: Fleece Jacket (49.99), Backpack (29.99), Bolt T-Shirt (24.99)
        inventoryPage.addProductToCart(3); // Fleece Jacket - $49.99
        inventoryPage.addProductToCart(0); // Backpack - $29.99
        inventoryPage.addProductToCart(2); // Bolt T-Shirt - $24.99

        Assertions.assertEquals("3", inventoryPage.getCartBadgeCountLocator().textContent());

        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        Assertions.assertEquals(3, cartPage.getCartItemCount());

        List<String> cartItems = cartPage.getCartItemNames();
        Assertions.assertTrue(cartItems.contains("Sauce Labs Fleece Jacket"));
        Assertions.assertTrue(cartItems.contains("Sauce Labs Backpack"));
        Assertions.assertTrue(cartItems.contains("Sauce Labs Bolt T-Shirt"));

        List<Double> pricesInCart = cartPage.getCartItemPrices().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        Assertions.assertEquals(priciestThree, pricesInCart);
    }

    @Test
    void shouldAddAlternatingProducts1st3rd5th(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        // Read the catalog prices before leaving the inventory page
        List<Double> catalogPrices = inventoryPage.getAllProductPrices();
        List<Double> alternating = IntStream.of(0, 2, 4)
                .mapToObj(catalogPrices::get)
                .sorted()
                .collect(Collectors.toList());

        // Add items at indices 0, 2, 4 (Backpack, Bolt T-Shirt, Onesie)
        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(2);
        inventoryPage.addProductToCart(4);

        Assertions.assertEquals("3", inventoryPage.getCartBadgeCountLocator().textContent());

        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        Assertions.assertEquals(3, cartPage.getCartItemCount());

        List<String> cartItems = cartPage.getCartItemNames();
        Assertions.assertTrue(cartItems.contains("Sauce Labs Backpack"));
        Assertions.assertTrue(cartItems.contains("Sauce Labs Bolt T-Shirt"));
        Assertions.assertTrue(cartItems.contains("Sauce Labs Onesie"));

        // Alternating means the odd-indexed catalog entries were picked
        List<Double> pricesInCart = cartPage.getCartItemPrices().stream()
                .sorted()
                .collect(Collectors.toList());
        Assertions.assertEquals(alternating, pricesInCart);
    }
}
