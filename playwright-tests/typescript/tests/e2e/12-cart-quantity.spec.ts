/**
 * Test 12: Cart badge increments by 1 on add and decrements by 1 on remove
 */
import { test, expect } from "../fixtures";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";

test("badge корзины растёт на 1 при добавлении и падает на 1 при удалении", async ({
  authenticatedPage,
}) => {
  const { inventoryPage, cartPage } = authenticatedPage;

  // Изначально корзина пуста — badge отсутствует
  await expect(inventoryPage.cartBadgeCount).toHaveCount(0);

  // Добавление товара → badge +1
  await inventoryPage.addProductToCart(0);
  await expect(inventoryPage.cartBadgeCount).toHaveText("1");

  // Добавление второго товара → badge ещё +1
  await inventoryPage.addProductToCart(1);
  await expect(inventoryPage.cartBadgeCount).toHaveText("2");

  // Переходим в корзину и удаляем один товар → badge −1
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  await cartPage.removeItem(0);
  await expect(cartPage.cartItems).toHaveCount(1);
  await expect(inventoryPage.cartBadgeCount).toHaveText("1");

  // Удаляем последний товар → badge пропадает
  await cartPage.removeItem(0);
  await expect(cartPage.cartItems).toHaveCount(0);
  await expect(inventoryPage.cartBadgeCount).toHaveCount(0);
});
