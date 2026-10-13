package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.InventoryPage;

/**
 * Specific item combinations.
 */
public class ItemCombinationsTest extends BaseTest {

    @Test
    @DisplayName("Adding the cheapest products and verifying the cart")
    void addCheapestProductsAndVerifyCart() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        // Record the catalog price of every product before picking the cheapest.
        List<Double> catalogPrices = inventoryPage.getAllProductPrices();
        List<Double> cheapest = sorted(catalogPrices, Comparator.naturalOrder()).subList(0, 3);

        // Onesie ($7.99), Bike Light ($9.99), Test.allTheThings() T-Shirt (Red) ($15.99).
        inventoryPage.addProductToCart(4);
        inventoryPage.addProductToCart(1);
        inventoryPage.addProductToCart(5);
        inventoryPage.waitForCartBadgeCount(3);

        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(3);

        List<String> items = cartPage.getCartItemNames();
        assertEquals(3, items.size());
        assertTrue(items.contains("Sauce Labs Onesie"), items.toString());
        assertTrue(items.contains("Sauce Labs Bike Light"), items.toString());
        assertTrue(items.contains("Test.allTheThings() T-Shirt (Red)"), items.toString());

        // The three cheapest in the catalogue are the three items in the cart.
        List<Double> pricesInCart = sorted(cartPage.getCartItemPrices(), Comparator.naturalOrder());
        assertEquals(cheapest, pricesInCart, "the cart holds the three cheapest products");
    }

    @Test
    @DisplayName("Adding the most expensive products and verifying the cart")
    void addMostExpensiveProductsAndVerifyCart() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        List<Double> catalogPrices = inventoryPage.getAllProductPrices();
        List<Double> priciest = sorted(catalogPrices, Comparator.reverseOrder()).subList(0, 3);

        // Fleece Jacket ($49.99), Backpack ($29.99), Bolt T-Shirt ($24.99).
        inventoryPage.addProductToCart(3);
        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(2);
        inventoryPage.waitForCartBadgeCount(3);

        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(3);

        List<String> items = cartPage.getCartItemNames();
        assertEquals(3, items.size());
        assertTrue(items.contains("Sauce Labs Fleece Jacket"), items.toString());
        assertTrue(items.contains("Sauce Labs Backpack"), items.toString());
        assertTrue(items.contains("Sauce Labs Bolt T-Shirt"), items.toString());

        List<Double> pricesInCart = sorted(cartPage.getCartItemPrices(), Comparator.reverseOrder());
        assertEquals(priciest, pricesInCart, "the cart holds the three most expensive products");
    }

    @Test
    @DisplayName("Adding alternating products and verifying the cart")
    void addAlternatingProductsAndVerifyCart() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        // Read the catalog prices before leaving the inventory page.
        List<Double> catalogPrices = inventoryPage.getAllProductPrices();
        List<Double> alternating = sorted(
            List.of(catalogPrices.get(0), catalogPrices.get(2), catalogPrices.get(4)),
            Comparator.naturalOrder());

        // Add items at indices 0, 2, 4 (Backpack, Bolt T-Shirt, Onesie).
        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(2);
        inventoryPage.addProductToCart(4);
        inventoryPage.waitForCartBadgeCount(3);

        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(3);

        List<String> items = cartPage.getCartItemNames();
        assertEquals(3, items.size());
        assertTrue(items.contains("Sauce Labs Backpack"), items.toString());
        assertTrue(items.contains("Sauce Labs Bolt T-Shirt"), items.toString());
        assertTrue(items.contains("Sauce Labs Onesie"), items.toString());

        // Alternating means the odd-indexed catalog entries were picked.
        List<Double> pricesInCart = sorted(cartPage.getCartItemPrices(), Comparator.naturalOrder());
        assertEquals(alternating, pricesInCart, "the cart holds the alternating products");
    }

    private List<Double> sorted(List<Double> prices, Comparator<Double> order) {
        List<Double> copy = new ArrayList<>(prices);
        copy.sort(order);
        return copy;
    }
}
