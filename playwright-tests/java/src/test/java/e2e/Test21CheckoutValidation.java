package e2e;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.CheckoutPage;
import pageobjects.InventoryPage;

/**
 * Checkout validation with empty fields.
 */
public class Test21CheckoutValidation extends BaseTest {

    @Test
    @DisplayName("The checkout form reports the missing fields one by one")
    void checkoutValidationWithEmptyFields() {
        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        inventoryPage.addProductToCart(0);
        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        cartPage.checkout();
        checkoutPage.waitForPageLoad();

        // Click Continue without filling in the fields.
        checkoutPage.clickContinueExpectingValidation();

        // We stay on the first checkout step.
        assertTrue(checkoutPage.isErrorMessageVisible(), "error is shown");

        // The error names the first missing field.
        String error = checkoutPage.getErrorMessage();
        assertTrue(error.contains("First Name"), "error mentions first name: " + error);

        // Fill in the first name only - the error now names the next missing field.
        checkoutPage.fillFirstName("Ivan");
        checkoutPage.clickContinueExpectingValidation();
        error = checkoutPage.getErrorMessage();
        assertTrue(error.contains("Last Name"), "error mentions last name: " + error);

        // Fill in the last name only - the error names the last missing field.
        checkoutPage.fillLastName("Ivanov");
        checkoutPage.clickContinueExpectingValidation();
        error = checkoutPage.getErrorMessage();
        assertTrue(error.contains("Postal Code"), "error mentions postal code: " + error);

        // With all three fields filled the form is accepted.
        checkoutPage.fillZipCode("100000");
        checkoutPage.clickContinue();
        checkoutPage.waitForPageLoad();

        // We should be on the overview page (checkout-step-two).
        String url = driver.getCurrentUrl();
        assertTrue(url.contains("checkout-step-two"), "moved to checkout overview: " + url);
    }
}