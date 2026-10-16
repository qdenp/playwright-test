package e2e;

import com.microsoft.playwright.*;
import pageobjects.CartPage;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Verify quantity stays at 1 per item (Sauce Demo doesn't allow editing quantity)
 */
class Test17QuantityRaceCondition extends BaseTest {

    @Test
    void shouldShowQuantity1PerItemAndOfferNoWayToEditIt(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        inventoryPage.addProductToCart(0); // Backpack
        inventoryPage.addProductToCart(2); // Bolt T-Shirt
        inventoryPage.addProductToCart(4); // Onesie

        Assertions.assertEquals("3", inventoryPage.getCartBadgeCountLocator().textContent());

        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        // Every item is displayed with quantity 1
        Assertions.assertEquals(3, cartPage.getCartItemCount());
        java.util.List<String> quantities = cartPage.getItemQuantities().allTextContents();
        Assertions.assertEquals(java.util.List.of("1", "1", "1"), quantities);

        // There is no quantity input to edit it with
        Assertions.assertEquals(0, cartPage.getQuantityInputs().count());
    }

    @Test
    void shouldAddEveryProductWhenTheAddButtonsAreClickedConcurrently(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        int buttonCount = inventoryPage.getAddToCartButtons().count();

        // Click all add-to-cart buttons without awaiting each one individually
        page.evaluate(
            "(count) => { const buttons = Array.from(document.querySelectorAll('.btn.btn_primary.btn_small.btn_inventory')); buttons.slice(0, count).forEach(button => button.click()); }",
            buttonCount
        );

        // All of them must have landed in the cart
        Assertions.assertEquals(String.valueOf(buttonCount), inventoryPage.getCartBadgeCountLocator().textContent());

        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        Assertions.assertEquals(buttonCount, cartPage.getCartItemCount());

        // And none of them can have a quantity other than 1
        java.util.List<String> expectedQuantities = new java.util.ArrayList<>();
        for (int i = 0; i < buttonCount; i++) {
            expectedQuantities.add("1");
        }
        java.util.List<String> actualQuantities = cartPage.getItemQuantities().allTextContents();
        Assertions.assertEquals(expectedQuantities, actualQuantities);
    }
}
