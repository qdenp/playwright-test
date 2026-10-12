import { test, expect } from "../fixtures.js";
import { CartPage } from "../page-objects/CartPage.js";
import { CheckoutPage } from "../page-objects/CheckoutPage.js";
import { timeouts } from "../config";
import fs from "fs";

/**
 * Exporting an order as PDF
 */
test("Exporting an order as PDF", async ({ authenticatedPage }) => {
  const { inventoryPage, cartPage, checkoutPage } = authenticatedPage;

  // Add products to the cart
  await inventoryPage.addProductToCart(0);
  await inventoryPage.addProductToCart(1);
  await expect(inventoryPage.cartBadgeCount).toHaveText("2");

  // Proceed to checkout
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  await cartPage.checkout();
  await checkoutPage.waitForPageLoad();

  await checkoutPage.fillFirstName("Ivan");
  await checkoutPage.fillLastName("Ivanov");
  await checkoutPage.fillZipCode("100000");
  await checkoutPage.clickContinue();

  // Wait for the move to checkout-step-two (Firefox can be slower)
  await expect(checkoutPage.finishButton).toBeVisible({
    timeout: timeouts.NAVIGATION,
  });

  // Click Finish
  await checkoutPage.clickFinish();

  // Wait for the confirmation page
  await expect(checkoutPage.completeTitle).toBeVisible({
    timeout: timeouts.NAVIGATION,
  });

  // Click Generate PDF and wait for the download
  const download = await checkoutPage.generatePdf();

  // Verify that the file is not empty
  const pdfPath = await download.path();
  expect(pdfPath).toBeTruthy();
  const stats = fs.statSync(pdfPath);
  expect(stats.size).toBeGreaterThan(0);
});
