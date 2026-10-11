import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";

/**
 * Удаление товара из корзины
 */
test("Удаление товара из корзины", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);
  const cartPage = new CartPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  await inventoryPage.addProductToCart(0);
  await inventoryPage.addProductToCart(1);
  await expect(inventoryPage.getCartBadgeCount()).resolves.toBe(2);

  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  const firstCartItem = page.locator('[data-test="inventory-item"]').first();
  await firstCartItem.getByRole("button", { name: "Remove" }).click();

  await expect(page.locator('[data-test="inventory-item"]')).toHaveCount(1);

  await page.reload();
  await cartPage.waitForPageLoad();
  await expect(page.locator(".shopping_cart_badge")).toHaveText("1");
});
