import { test, expect } from "../fixtures";
import { timeouts } from "../config";

/**
 * Полный flow покупки (E2E)
 */
test("Полный flow покупки (E2E)", async ({ authenticatedPage }) => {
  const { inventoryPage, cartPage, checkoutPage } = authenticatedPage;

  // Add 2 products to cart
  await inventoryPage.addProductToCart(0);
  await inventoryPage.addProductToCart(1);
  await expect(inventoryPage.cartBadgeCount).toHaveText("2");

  // Open cart
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  // Checkout
  await cartPage.checkout();
  await checkoutPage.waitForPageLoad();

  // Fill info
  await checkoutPage.fillInfo("Иван", "Иванов", "100000");
  await checkoutPage.clickContinue();

  // Complete order
  await expect(checkoutPage.finishButton).toBeVisible({ timeout: timeouts.DEFAULT });

  // Проверяем суммы заказа: item total = сумма цен товаров
  const itemPrices = await checkoutPage.getSummaryItemPrices();
  expect(itemPrices).toHaveLength(2);

  const itemsSum = itemPrices.reduce((sum, price) => sum + price, 0);
  const itemsTotal = await checkoutPage.getItemsTotal();
  expect(itemsTotal).toBeCloseTo(itemsSum, 2);

  // tax = 8% от item total
  const tax = await checkoutPage.getTax();
  expect(tax).toBeCloseTo(itemsTotal * 0.08, 2);

  // total = item total + tax
  const total = await checkoutPage.getTotal();
  expect(total).toBeCloseTo(itemsTotal + tax, 2);

  await checkoutPage.clickFinish();

  // Verify success
  await expect(checkoutPage.completeTitle).toBeVisible({ timeout: timeouts.DEFAULT });
  await expect(checkoutPage.completeTitle).toContainText(
    "Thank you for your order",
  );
});
