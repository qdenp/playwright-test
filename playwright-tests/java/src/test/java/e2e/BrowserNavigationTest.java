package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.CheckoutPage;
import pageobjects.InventoryPage;

/**
 * Browser navigation tests.
 */
public class BrowserNavigationTest extends BaseTest {

    @Test
    @DisplayName("The browser back button returns from the cart to the catalogue")
    void backButtonFromCartToInventory() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        inventoryPage.addProductToCart(0);
        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        // Go back to inventory.
        driver.navigate().back();
        inventoryPage.waitForPageLoad();

        assertTrue(driver.getCurrentUrl().contains("inventory"), "the catalogue is shown again");
        assertTrue(inventoryPage.getTitleText().contains("Products"), "the catalogue title is back");
    }

    @Test
    @DisplayName("The browser forward button returns to the cart after going back")
    void forwardButtonAfterBack() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        inventoryPage.addProductToCart(0);
        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        // Go back.
        driver.navigate().back();
        inventoryPage.waitForPageLoad();

        // Go forward.
        driver.navigate().forward();
        cartPage.waitForPageLoad();

        assertTrue(driver.getCurrentUrl().contains("cart"), "the cart is shown again");
    }

    @Test
    @DisplayName("The browser back button returns from the checkout to the cart")
    void backButtonFromCheckoutToCart() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        inventoryPage.addProductToCart(0);
        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        cartPage.checkout();
        checkoutPage.waitForPageLoad();

        // Go back to cart.
        driver.navigate().back();
        cartPage.waitForPageLoad();

        assertTrue(driver.getCurrentUrl().contains("cart"), "the cart is shown again");
    }

    @Test
    @DisplayName("The cart survives several back and forward navigations")
    void cartStateSurvivesBackAndForward() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);

        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(1);
        inventoryPage.waitForCartBadgeCount(2);

        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        // Navigate back and forth.
        driver.navigate().back();
        inventoryPage.waitForPageLoad();
        driver.navigate().forward();
        cartPage.waitForPageLoad();

        // Cart should still have 2 items.
        assertEquals(2, inventoryPage.getCartBadgeCount(), "the cart still holds two products");
        assertTrue(driver.getCurrentUrl().contains("cart"));
    }
}
