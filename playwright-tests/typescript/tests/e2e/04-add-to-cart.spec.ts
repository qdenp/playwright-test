import { test, expect } from "../fixtures";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";

/**
 * Test 04: Добавление товара в корзину
 */
test("Добавление товара в корзину", async ({ authenticatedPage }) => {
  const { inventoryPage, cartPage } = authenticatedPage;

  // Добавить первый товар
  await inventoryPage.addProductToCart(0);
  await expect(
    inventoryPage.cartBadge.locator(".shopping_cart_badge"),
  ).toHaveText("1");

  // Открыть корзину и проверить первый товар
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  let cartItemCount = await cartPage.getCartItemCount();
  expect(cartItemCount).toBe(1);
  let cartItemNames = await cartPage.getCartItemNames();
  let firstItemName = cartItemNames[0];
  expect(firstItemName).toBeTruthy();
  expect(firstItemName!.length).toBeGreaterThan(0);

  // Вернуться на страницу товаров
  await cartPage.continueShopping();
  await inventoryPage.waitForPageLoad();

  // Добавить второй товар
  await inventoryPage.addProductToCart(1);
  await expect(
    inventoryPage.cartBadge.locator(".shopping_cart_badge"),
  ).toHaveText("2");

  // Открыть корзину и проверить оба товара
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  cartItemCount = await cartPage.getCartItemCount();
  expect(cartItemCount).toBe(2);
  cartItemNames = await cartPage.getCartItemNames();
  let secondItemName = cartItemNames[1];
  expect(secondItemName).toBeTruthy();
  expect(secondItemName!.length).toBeGreaterThan(0);
  // Товары должны быть разными
  expect(firstItemName).not.toBe(secondItemName);
});
