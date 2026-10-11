import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";
import { CheckoutPage } from "../page-objects/CheckoutPage.js";

/**
 * Полный flow покупки (E2E)
 */
test("Полный flow покупки (E2E)", { timeout: 60000 }, async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);
  const cartPage = new CartPage(page);
  const checkoutPage = new CheckoutPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  await inventoryPage.addProductToCart(0);
  await inventoryPage.addProductToCart(1);
  await expect(inventoryPage.getCartBadgeCount()).resolves.toBe(2);

  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  await cartPage.checkout();
  await checkoutPage.waitForPageLoad();

  await checkoutPage.fillInfo("Иван", "Иванов", "100000");
  await checkoutPage.clickContinue();

  // Ожидаем кнопку Finish
  await expect(checkoutPage.finishButton).toBeVisible({ timeout: 10000 });

  await checkoutPage.clickFinish();

  await expect(checkoutPage.thankYouMessage).toBeVisible({ timeout: 10000 });
  await expect(checkoutPage.completeText).toContainText("Your order has been");
});
