package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.InventoryPage;

/**
 * Reset App State - the hamburger menu entry that clears the stored cart.
 */
public class Test28ResetAppState extends BaseTest {

    @Test
    @DisplayName("Reset App State clears the cart badge")
    void resetAppStateClearsTheBadge() {
        InventoryPage inventoryPage = loginAsStandardUser();

        for (int i = 0; i < 3; i++) {
            inventoryPage.addProductToCart(i);
        }
        inventoryPage.waitForCartBadgeCount(3);

        inventoryPage.resetAppState();

        // The badge disappears immediately - the cart is empty.
        inventoryPage.waitForCartBadgeAbsent();
        assertEquals(0, inventoryPage.getCartBadgeCount(), "the badge is gone");
    }

    /**
     * SAUCE_DEMO_BUG: Reset App State requires a page reload to fully work.
     *
     * The test verifies two things that should happen immediately after clicking
     * "Reset App State" but currently require a page reload on the GitHub Pages
     * build:
     *
     * 1. While on the cart page - cart items are removed (not just the badge)
     * 2. While on the inventory page - "Add to Cart" buttons reappear
     *
     * On the real SauceDemo (server-side state) these work instantly.
     * On GitHub Pages the SPA needs a reload, but reload redirects to login.
     *
     * Once the site bug is fixed: remove {@code @Disabled} and restore the body.
     */
    @Test
    @DisplayName("Reset App State clears the stored cart, not just the badge")
    @Disabled("SAUCE_DEMO_BUG: Reset App State clears localStorage on GitHub Pages; a full page reload loses the product catalog")
    void resetAppStateClearsTheStoredCart() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        for (int i = 0; i < 3; i++) {
            inventoryPage.addProductToCart(i);
        }
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(3);

        inventoryPage.resetAppState();

        // Re-open the cart so the page re-reads the stored state.
        openPath("/cart.html");
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(0);

        // The catalogue itself is untouched.
        inventoryPage.goTo();
        int count = inventoryPage.getProductCount();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count);
        inventoryPage.waitForCartBadgeAbsent();
        assertTrue(inventoryPage.isFirstProductVisible(), "the catalogue is intact");
    }
}
