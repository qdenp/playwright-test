import { test, expect } from "../fixtures";
import { CartPage } from "../page-objects/CartPage.js";

/**
 * Добавление товара в корзину
 */
test("Добавление товара в корзину", async ({ authenticatedPage }) => {
  const { inventoryPage, cartPage } = authenticatedPage;

  // Добавить первый товар — badge показывает 1
  await inventoryPage.addProductToCart(0);
  await expect(inventoryPage.cartBadgeCount).toHaveText("1");

  // Открыть корзину и проверить первый товар
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(1);

  const firstCartNames = await cartPage.getCartItemNames();
  expect(firstCartNames[0]).toBeTruthy();
  expect(firstCartNames[0]!.length).toBeGreaterThan(0);

  // Вернуться на страницу товаров
  await cartPage.continueShopping();
  await inventoryPage.waitForPageLoad();

  // Добавить второй товар — badge показывает 2
  await inventoryPage.addProductToCart(1);
  await expect(inventoryPage.cartBadgeCount).toHaveText("2");

  // Открыть корзину и проверить оба товара
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  const bothCartNames = await cartPage.getCartItemNames();
  expect(bothCartNames[1]).toBeTruthy();
  expect(bothCartNames[1]!.length).toBeGreaterThan(0);
  // Товары должны быть разными
  expect(bothCartNames[0]).not.toBe(bothCartNames[1]);
});
