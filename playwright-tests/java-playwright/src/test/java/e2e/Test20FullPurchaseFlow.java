package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.CartPage;
import pageobjects.CheckoutPage;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

import java.util.List;

/**
 * Full purchase flow (E2E)
 */
class Test20FullPurchaseFlow extends BaseTest {

    @Test
    void fullPurchaseFlowE2E(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;
        CheckoutPage checkoutPage = authenticatedPage.checkoutPage;

        // Add 2 products to cart
        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(1);
        Assertions.assertEquals("2", inventoryPage.getCartBadgeCountLocator().textContent());

        // Open cart
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        Assertions.assertEquals(2, cartPage.getCartItemCount());

        // Checkout
        cartPage.checkout();
        checkoutPage.waitForPageLoad();

        // Fill info
        checkoutPage.fillInfo("Ivan", "Ivanov", "100000");
        checkoutPage.clickContinue();

        // Complete order
        Assertions.assertTrue(checkoutPage.getFinishButton().isVisible());

        // Verify the order totals: item total equals the sum of the item prices
        List<Double> itemPrices = checkoutPage.getSummaryItemPrices();
        Assertions.assertEquals(2, itemPrices.size());

        double itemsSum = itemPrices.stream().mapToDouble(Double::doubleValue).sum();
        double itemsTotal = checkoutPage.getItemsTotal();
        Assertions.assertEquals(itemsSum, itemsTotal, 0.01);

        // tax = 8% of the item total
        double tax = checkoutPage.getTax();
        Assertions.assertEquals(itemsTotal * 0.08, tax, 0.01);

        // total = item total + tax
        double total = checkoutPage.getTotal();
        Assertions.assertEquals(itemsTotal + tax, total, 0.01);

        checkoutPage.clickFinish();

        // Verify success
        Assertions.assertTrue(checkoutPage.getCompleteTitle().isVisible());
        Assertions.assertTrue(checkoutPage.getCompleteTitle().textContent().contains(TestConfig.COMPLETE_ORDER_TEXT));
    }
}
