import { test, expect } from "../fixtures.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";
import { CheckoutPage } from "../page-objects/CheckoutPage.js";

/**
 * Checkout form validation with empty fields
 */
test("Checkout validation with empty fields", async ({ authenticatedPage }) => {
  const { inventoryPage, cartPage, checkoutPage } = authenticatedPage;

  await inventoryPage.addProductToCart(0);
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();

  await cartPage.checkout();
  await checkoutPage.waitForPageLoad();

  // Click Continue without filling in the fields
  await checkoutPage.clickContinue();

  // Expect validation errors for all fields
  await expect(checkoutPage.firstNameInput).toBeVisible();
  await expect(checkoutPage.lastNameInput).toBeVisible();
  await expect(checkoutPage.zipInput).toBeVisible();

  const errorMessage = await checkoutPage.getErrorMessage();
  expect(errorMessage).toBeTruthy();
});
