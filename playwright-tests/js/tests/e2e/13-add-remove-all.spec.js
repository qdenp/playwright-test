import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";

/**
 * Тест 13: Добавление и удаление всех товаров
 */
test("Тест 13: Добавление и удаление всех товаров", { timeout: 120000 }, async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);
  const cartPage = new CartPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  const productCount = await inventoryPage.getProductCount();

  // Добавляем все товары
  for (let i = 0; i < productCount; i++) {
    await inventoryPage.addProductToCart(i);
  }

  await expect(inventoryPage.getCartBadgeCount()).resolves.toBe(productCount);

  // Переходим в корзину
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(productCount);

  // Удаляем все товары
  for (let i = 0; i < productCount; i++) {
    await cartPage.removeItem(0);
    await expect(page.locator(".cart_item")).toHaveCount(productCount - 1 - i);
  }

  // Корзина должна быть пуста
  await expect(cartPage.cartItems).toHaveCount(0);

  // Перезагружаем для проверки badge
  await page.reload();
  await expect(inventoryPage.getCartBadgeCount()).resolves.toBe(0);
});
