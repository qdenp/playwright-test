import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";

/**
 * Тест 12: Изменение количества товара в корзине
 */
test("Тест 12: Изменение количества товара в корзине", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);
  const cartPage = new CartPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  await inventoryPage.addProductToCart(0);
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();

  await expect(cartPage.cartItems).toHaveCount(1);

  const productName = await cartPage.cartItems
    .first()
    .locator('[data-test="inventory-item-name"]')
    .textContent();
  expect(productName).toBeTruthy();
});
