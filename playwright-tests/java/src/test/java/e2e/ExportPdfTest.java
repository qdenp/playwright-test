package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.CheckoutPage;
import pageobjects.InventoryPage;

/**
 * Exporting an order as PDF.
 *
 * SauceDemo really generates a PDF on the confirmation page, so the scenario
 * checks the downloaded file and not only that the button is clickable.
 */
public class ExportPdfTest extends BaseTest {

    @Test
    @DisplayName("Exporting an order as PDF downloads a readable file")
    void exportingAnOrderAsPdf() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        // Add products to the cart.
        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(1);
        inventoryPage.waitForCartBadgeCount(2);

        // Proceed to checkout.
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

        // The order overview accepts the order.
        checkoutPage.waitForFinishButtonVisible();
        checkoutPage.clickFinish();

        // The confirmation page carries the PDF export.
        assertTrue(checkoutPage.getCompleteHeaderText().contains("Thank you for your order"),
            "the order is confirmed");

        // Click Generate PDF and wait for the download to land on disk.
        checkoutPage.clickGeneratePdf();

        Path pdf = waitForDownloadedFile(TestConfig.NAVIGATION_TIMEOUT);
        assertNotNull(pdf, "the PDF order is downloaded");
        try {
            assertTrue(Files.size(pdf) > 0, "the downloaded file is not empty");
            byte[] header = new byte[5];
            assertEquals(5, Files.newInputStream(pdf).read(header), "the file starts with the PDF magic bytes");
            assertEquals("%PDF-", new String(header));
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Cannot read the downloaded PDF", e);
        }
    }
}
