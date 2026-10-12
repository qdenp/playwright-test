import { test, expect } from "../fixtures";

test("Checkout validation with empty fields", async ({ authenticatedPage }) => {
  const { inventoryPage, cartPage, checkoutPage } = authenticatedPage;

  await inventoryPage.addProductToCart(0);
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();

  await cartPage.checkout();
  await checkoutPage.waitForPageLoad();
  await expect(checkoutPage.firstNameInput).toBeVisible();

  // Click Continue without filling in the fields
  await checkoutPage.clickContinue();

  // We stay on the first checkout step
  await expect(checkoutPage.firstNameInput).toBeVisible();
  await expect(checkoutPage.continueButton).toBeVisible();

  // The error names the first missing field
  await expect(checkoutPage.errorMessage).toContainText("First Name is required");

  // Fill in the first name only - the error now names the next missing field
  await checkoutPage.fillFirstName("Ivan");
  await checkoutPage.clickContinue();
  await expect(checkoutPage.errorMessage).toContainText("Last Name is required");

  // Fill in the last name only - the error names the last missing field
  await checkoutPage.fillLastName("Ivanov");
  await checkoutPage.clickContinue();
  await expect(checkoutPage.errorMessage).toContainText("Postal Code is required");

  // With all three fields filled the form is accepted
  await checkoutPage.fillZipCode("100000");
  await checkoutPage.clickContinue();
  await expect(checkoutPage.finishButton).toBeVisible();
});
