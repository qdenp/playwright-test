import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";
import { CheckoutPage } from "../page-objects/CheckoutPage.js";
import { timeouts } from "../config.js";

/**
 * Full purchase flow (E2E)
 */
test("Full purchase flow (E2E)", { timeout: 60000 }, async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);
  const cartPage = new CartPage(page);
  const checkoutPage = new CheckoutPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  await inventoryPage.addProductToCart(0);
  await inventoryPage.addProductToCart(1);
  await expect(inventoryPage.cartBadgeCount).toHaveText("2");

  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  await cartPage.checkout();
  await checkoutPage.waitForPageLoad();

  await checkoutPage.fillInfo("Ivan", "Ivanov", "100000");
  await checkoutPage.clickContinue();

  // Wait for the Finish button
  await expect(checkoutPage.finishButton).toBeVisible({ timeout: timeouts.DEFAULT });

  // Verify the order totals: item total equals the sum of the item prices
  const itemPrices = await checkoutPage.getSummaryItemPrices();
  expect(itemPrices).toHaveLength(2);

  const itemsSum = itemPrices.reduce((sum, price) => sum + price, 0);
  const itemsTotal = await checkoutPage.getItemsTotal();
  expect(itemsTotal).toBeCloseTo(itemsSum, 2);

  // tax = 8% of the item total
  const tax = await checkoutPage.getTax();
  expect(tax).toBeCloseTo(itemsTotal * 0.08, 2);

  // total = item total + tax
  const total = await checkoutPage.getTotal();
  expect(total).toBeCloseTo(itemsTotal + tax, 2);

  await checkoutPage.clickFinish();

  await expect(checkoutPage.completeTitle).toBeVisible({ timeout: timeouts.DEFAULT });
  await expect(checkoutPage.completeTitle).toContainText("Thank you for your order");
});
