package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.CartPage;
import pageobjects.CheckoutPage;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;

/**
 * Exporting an order as PDF
 */
class Test22ExportPdf extends BaseTest {

    @Test
    void exportingAnOrderAsPdf(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;
        CheckoutPage checkoutPage = authenticatedPage.checkoutPage;

        // Add products to the cart
        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(1);
        Assertions.assertEquals("2", inventoryPage.getCartBadgeCountLocator().textContent());

        // Proceed to checkout
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        Assertions.assertEquals(2, cartPage.getCartItemCount());

        cartPage.checkout();
        checkoutPage.waitForPageLoad();

        checkoutPage.fillFirstName("Ivan");
        checkoutPage.fillLastName("Ivanov");
        checkoutPage.fillZipCode("100000");
        checkoutPage.clickContinue();

        // Wait for the move to checkout-step-two (Firefox can be slower)
        Assertions.assertTrue(checkoutPage.getFinishButton().isVisible());

        // Click Finish
        checkoutPage.clickFinish();

        // Wait for the confirmation page
        Assertions.assertTrue(checkoutPage.getCompleteTitle().isVisible());

        // Click Generate PDF and wait for the download
        Download download = checkoutPage.generatePdf();

        // Verify that the file is not empty
        java.nio.file.Path pdfPath = download.path();
        Assertions.assertNotNull(pdfPath);
        long size;
        try {
            size = java.nio.file.Files.size(pdfPath);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertTrue(size > 0);
    }
}
