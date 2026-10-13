package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.InventoryPage;

/**
 * Reset App State - the hamburger menu entry that clears the stored cart.
 */
public class ResetAppStateTest extends BaseTest {

    @Test
    @DisplayName("Reset App State clears the cart badge")
    void resetAppStateClearsTheBadge() {
        InventoryPage inventoryPage = loginAsStandardUser();

        for (int i = 0; i < 3; i++) {
            inventoryPage.addProductToCart(i);
        }
        inventoryPage.waitForCartBadgeCount(3);

        inventoryPage.openHamburgerMenu();
        inventoryPage.resetAppState();
        inventoryPage.closeHamburgerMenu();

        // The badge disappears immediately - the cart is empty.
        inventoryPage.waitForCartBadgeAbsent();
        assertEquals(0, inventoryPage.getCartBadgeCount(), "the badge is gone");
    }

    @Test
    @DisplayName("Reset App State clears the stored cart, not just the badge")
    void resetAppStateClearsTheStoredCart() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        for (int i = 0; i < 3; i++) {
            inventoryPage.addProductToCart(i);
        }
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(3);

        inventoryPage.openHamburgerMenu();
        inventoryPage.resetAppState();
        inventoryPage.closeHamburgerMenu();

        // Re-open the cart so the page re-reads the stored state.
        openPath("/cart.html");
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(0);

        // The catalogue itself is untouched.
        openPath("/inventory.html");
        inventoryPage.waitForPageLoad();
        inventoryPage.waitForProductCount(TestConfig.EXPECTED_PRODUCT_COUNT);
        inventoryPage.waitForCartBadgeAbsent();
        assertTrue(inventoryPage.isFirstProductVisible(), "the catalogue is intact");
    }
}
