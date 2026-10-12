import { test, expect } from "../fixtures.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";

/**
 * Добавление и удаление всех товаров
 */
test("Добавление и удаление всех товаров", async ({ authenticatedPage }) => {
  const { inventoryPage, cartPage } = authenticatedPage;

  const productCount = await inventoryPage.getProductCount();
  await expect(inventoryPage.products).toHaveCount(productCount);

  // Добавляем все товары
  for (let i = 0; i < productCount; i++) {
    await inventoryPage.addProductToCart(i);
  }

  await expect(inventoryPage.cartBadgeCount).toHaveText(String(productCount));

  // Переходим в корзину
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(productCount);

  // Удаляем все товары
  for (let i = 0; i < productCount; i++) {
    await cartPage.removeItem(0);
    await expect(cartPage.cartItems).toHaveCount(productCount - 1 - i);
  }

  // Корзина должна быть пуста
  await expect(cartPage.cartItems).toHaveCount(0);

  // Перезагружаем для проверки badge
  await authenticatedPage.page.reload();
  await expect(inventoryPage.cartBadgeCount).toHaveCount(0);
});
