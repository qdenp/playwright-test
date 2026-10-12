import { test, expect } from "../fixtures.js";

/**
 * Удаление товара из корзины
 */
test("Удаление товара из корзины", async ({ authenticatedPage }) => {
  const { inventoryPage, cartPage } = authenticatedPage;

  await inventoryPage.addProductToCart(0);
  await inventoryPage.addProductToCart(1);
  await expect(inventoryPage.cartBadgeCount).toHaveText("2");

  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  await cartPage.removeItem(0);
  await expect(cartPage.cartItems).toHaveCount(1);

  await authenticatedPage.page.reload();
  await cartPage.waitForPageLoad();
  await expect(inventoryPage.cartBadgeCount).toHaveText("1");
});
