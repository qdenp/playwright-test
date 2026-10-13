package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.InventoryPage;

/**
 * Add all 6 products and verify.
 */
public class AddAllSixProductsTest extends BaseTest {

    private static final List<String> EXPECTED_NAMES = List.of(
        "Sauce Labs Backpack",
        "Sauce Labs Bike Light",
        "Sauce Labs Bolt T-Shirt",
        "Sauce Labs Fleece Jacket",
        "Sauce Labs Onesie",
        "Test.allTheThings() T-Shirt (Red)");

    @Test
    @DisplayName("Adding all products fills the badge and the cart")
    void addAllProductsAndVerifyCart() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        addEverything(inventoryPage);
        inventoryPage.waitForCartBadgeCount(TestConfig.EXPECTED_PRODUCT_COUNT);

        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(TestConfig.EXPECTED_PRODUCT_COUNT);
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, cartPage.getCartItemNames().size());
    }

    @Test
    @DisplayName("The catalogue holds the expected number of priced products")
    void verifyProductCountAndPrices() {
        InventoryPage inventoryPage = loginAsStandardUser();

        inventoryPage.waitForProductCount(TestConfig.EXPECTED_PRODUCT_COUNT);
        inventoryPage.waitForProductPriceCount(TestConfig.EXPECTED_PRODUCT_COUNT);

        List<Double> prices = inventoryPage.getAllProductPrices();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, prices.size());
        for (double price : prices) {
            assertTrue(price > 0, "price of " + InventoryPage.money(price) + " must be positive");
            assertTrue(price <= 100, "price of " + InventoryPage.money(price) + " must stay in a sane range");
        }
    }

    @Test
    @DisplayName("Adding all products puts the whole catalogue into the cart")
    void addAllProductsAndVerifyNames() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        addEverything(inventoryPage);

        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(TestConfig.EXPECTED_PRODUCT_COUNT);

        List<String> cartItems = cartPage.getCartItemNames();
        assertEquals(EXPECTED_NAMES.size(), cartItems.size());
        for (String name : EXPECTED_NAMES) {
            assertTrue(cartItems.contains(name), "the cart contains " + name);
        }
    }

    private void addEverything(InventoryPage inventoryPage) {
        for (int i = 0; i < TestConfig.EXPECTED_PRODUCT_COUNT; i++) {
            inventoryPage.addProductToCart(i);
        }
    }
}
