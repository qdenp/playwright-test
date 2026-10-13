package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.CheckoutPage;
import pageobjects.InventoryPage;

/**
 * Full purchase flow (E2E).
 */
public class FullPurchaseFlowTest extends BaseTest {

    /** Money is rounded to cents, so half a cent of tolerance is enough. */
    private static final double TAX_TOLERANCE = 0.005;

    @Test
    @DisplayName("Full purchase flow - from login to the order confirmation")
    void fullPurchaseFlow() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(1);
        inventoryPage.waitForCartBadgeCount(2);

        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        cartPage.waitForCartItemCount(2);

        cartPage.checkout();
        checkoutPage.waitForPageLoad();

        checkoutPage.fillInfo(
            TestConfig.CHECKOUT_FIRST_NAME,
            TestConfig.CHECKOUT_LAST_NAME,
            TestConfig.CHECKOUT_POSTAL_CODE);
        checkoutPage.clickContinue();

        // Wait for the move to the checkout overview.
        checkoutPage.waitForFinishButtonVisible();

        // Verify the order totals: item total equals the sum of the item prices.
        checkoutPage.waitForSummaryItemCount(2);
        var itemPrices = checkoutPage.getSummaryItemPrices();
        assertEquals(2, itemPrices.size());

        double itemsSum = itemPrices.stream().mapToDouble(Double::doubleValue).sum();
        double itemsTotal = checkoutPage.getItemsTotal();
        assertEquals(itemsSum, itemsTotal, TAX_TOLERANCE, "the item total is the sum of the item prices");

        // tax = 8% of the item total.
        double tax = checkoutPage.getTax();
        assertEquals(itemsTotal * TestConfig.CHECKOUT_TAX_RATE, tax, TAX_TOLERANCE,
            "the tax is 8% of the item total, rounded to cents");

        // total = item total + tax.
        double total = checkoutPage.getTotal();
        assertEquals(itemsTotal + tax, total, TAX_TOLERANCE, "the total is the item total plus the tax");

        checkoutPage.clickFinish();
        String header = checkoutPage.getCompleteHeaderText();
        assertTrue(header.contains("Thank you for your order"), "the confirmation reads: " + header);
    }
}
