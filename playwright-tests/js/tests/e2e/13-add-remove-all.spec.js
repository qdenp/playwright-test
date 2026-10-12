import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";
import { EXPECTED_PRODUCT_COUNT } from "../config.js";

/**
 * Тест 13: Добавление и удаление всех товаров
 */
test("Тест 13: Добавление и удаление всех товаров", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);
  const cartPage = new CartPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);

  // Добавляем все товары
  for (let i = 0; i < EXPECTED_PRODUCT_COUNT; i++) {
    await inventoryPage.addProductToCart(i);
  }

  await expect(inventoryPage.cartBadgeCount).toHaveText(String(EXPECTED_PRODUCT_COUNT));

  // Переходим в корзину
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(EXPECTED_PRODUCT_COUNT);

  // Удаляем все товары
  for (let i = 0; i < EXPECTED_PRODUCT_COUNT; i++) {
    await cartPage.removeItem(0);
    await expect(cartPage.cartItems).toHaveCount(EXPECTED_PRODUCT_COUNT - 1 - i);
  }

  // Корзина должна быть пуста
  await expect(cartPage.cartItems).toHaveCount(0);

  // Перезагружаем для проверки badge
  await page.reload();
  await expect(inventoryPage.cartBadgeCount).toHaveCount(0);
});
