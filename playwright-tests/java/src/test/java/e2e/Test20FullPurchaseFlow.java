package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import pageobjects.CartPage;
import pageobjects.CheckoutPage;
import pageobjects.InventoryPage;

/**
 * Full purchase flow (E2E).
 */
public class Test20FullPurchaseFlow extends BaseTest {

    // TAX_TOLERANCE is in TestConfig.TAX_TOLERANCE

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

        checkoutPage.fillFirstName(TestConfig.CHECKOUT_FIRST_NAME);
        checkoutPage.fillLastName(TestConfig.CHECKOUT_LAST_NAME);
        checkoutPage.fillZipCode(TestConfig.CHECKOUT_POSTAL_CODE);
        checkoutPage.clickContinue();
        checkoutPage.waitForPageLoad();
        
        // Wait for price elements to be visible AND have text (not just empty)
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.NAVIGATION_TIMEOUT));
        longWait.until(d -> {
            var elements = d.findElements(checkoutPage.summaryItemPrices);
            return !elements.isEmpty() && elements.stream().anyMatch(e -> e.getText() != null && !e.getText().isEmpty());
        });
        
        checkoutPage.waitForPageLoad();

        // Verify the order totals: item total equals the sum of the item prices.
        var itemPrices = checkoutPage.getSummaryItemPrices();
        assertEquals(2, itemPrices.size());

        double itemsSum = itemPrices.stream().mapToDouble(Double::doubleValue).sum();
        double itemsTotal = checkoutPage.getSubtotal();
        assertEquals(itemsSum, itemsTotal, TestConfig.TAX_TOLERANCE, "the item total is the sum of the item prices");

        // tax = 8% of the item total.
        double tax = checkoutPage.getTax();
        assertEquals(itemsTotal * TestConfig.CHECKOUT_TAX_RATE, tax, TestConfig.TAX_TOLERANCE,
            "the tax is 8% of the item total, rounded to cents");

        // total = item total + tax.
        double total = checkoutPage.getTotal();
        assertEquals(itemsTotal + tax, total, TestConfig.TAX_TOLERANCE, "the total is the item total plus the tax");

        checkoutPage.clickFinish();
        String header = checkoutPage.getCompleteTitle();
        assertTrue(header.contains(TestConfig.COMPLETE_ORDER_TEXT), "the confirmation reads: " + header);
    }
}