package e2e;

import com.microsoft.playwright.*;
import pageobjects.CartPage;
import pageobjects.CheckoutPage;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Checkout validation with empty fields
 */
class Test21CheckoutValidation extends BaseTest {

    @Test
    void checkoutValidationWithEmptyFields(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;
        CheckoutPage checkoutPage = authenticatedPage.checkoutPage;

        inventoryPage.addProductToCart(0);
        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        cartPage.checkout();
        checkoutPage.waitForPageLoad();
        Assertions.assertTrue(checkoutPage.getFirstNameInput().isVisible());

        // Click Continue without filling in the fields
        checkoutPage.clickContinue();

        // We stay on the first checkout step
        Assertions.assertTrue(checkoutPage.getFirstNameInput().isVisible());
        Assertions.assertTrue(checkoutPage.getContinueButton().isVisible());

        // The error names the first missing field
        Assertions.assertTrue(checkoutPage.getErrorMessageLocator().textContent().contains("First Name is required"));

        // Fill in the first name only - the error now names the next missing field
        checkoutPage.fillFirstName("Ivan");
        checkoutPage.clickContinue();
        Assertions.assertTrue(checkoutPage.getErrorMessageLocator().textContent().contains("Last Name is required"));

        // Fill in the last name only - the error names the last missing field
        checkoutPage.fillLastName("Ivanov");
        checkoutPage.clickContinue();
        Assertions.assertTrue(checkoutPage.getErrorMessageLocator().textContent().contains("Postal Code is required"));

        // With all three fields filled the form is accepted
        checkoutPage.fillZipCode("100000");
        checkoutPage.clickContinue();
        Assertions.assertTrue(checkoutPage.getFinishButton().isVisible());
    }
}
