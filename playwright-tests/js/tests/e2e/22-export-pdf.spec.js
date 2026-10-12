import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";
import { CheckoutPage } from "../page-objects/CheckoutPage.js";
import { timeouts } from "../config.js";
import fs from "fs";

/**
 * Exporting an order as PDF
 */
test("Exporting an order as PDF", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);
  const cartPage = new CartPage(page);
  const checkoutPage = new CheckoutPage(page);

  // Add products to the cart
  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  await inventoryPage.addProductToCart(0);
  await inventoryPage.addProductToCart(1);
  await expect(inventoryPage.cartBadgeCount).toHaveText("2");

  // Proceed to checkout
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  await cartPage.checkout();
  await checkoutPage.waitForPageLoad();

  await checkoutPage.fillInfo("Ivan", "Ivanov", "100000");
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

  // Save the PDF
  const pdfPath = await download.path();
  expect(pdfPath).toBeTruthy();

  // Verify the file exists and it is not empty
  const stats = fs.statSync(pdfPath);
  expect(stats.size).toBeGreaterThan(0);
});
