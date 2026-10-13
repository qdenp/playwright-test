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
public class CheckoutValidationTest extends BaseTest {

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
        assertTrue(checkoutPage.isFirstNameInputVisible(), "the checkout form is open");

        // Click Continue without filling in the fields.
        checkoutPage.clickContinue();

        // We stay on the first checkout step.
        assertTrue(checkoutPage.isFirstNameInputVisible(), "the form stays open");
        assertTrue(checkoutPage.isContinueButtonVisible(), "the form can still be submitted");

        // The error names the first missing field.
        assertFalse(checkoutPage.getErrorMessageText().isEmpty());
        checkoutPage.waitForErrorMessageText("First Name is required");

        // Fill in the first name only - the error now names the next missing field.
        checkoutPage.fillFirstName("Ivan");
        checkoutPage.clickContinue();
        checkoutPage.waitForErrorMessageText("Last Name is required");

        // Fill in the last name only - the error names the last missing field.
        checkoutPage.fillLastName("Ivanov");
        checkoutPage.clickContinue();
        checkoutPage.waitForErrorMessageText("Postal Code is required");

        // With all three fields filled the form is accepted.
        checkoutPage.fillPostalCode("100000");
        checkoutPage.clickContinue();
        checkoutPage.waitForFinishButtonVisible();
        assertTrue(checkoutPage.isFinishButtonVisible(), "the accepted form moves to the overview");
    }
}
