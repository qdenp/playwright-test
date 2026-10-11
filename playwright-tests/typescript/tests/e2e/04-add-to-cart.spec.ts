import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Test 04: Добавление товара в корзину
 */
test("Добавление товара в корзину", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Add first product
  await inventoryPage.addProductToCart(0);

  // Verify badge shows 1
  await expect(
    inventoryPage.cartBadge.locator(".shopping_cart_badge"),
  ).toHaveText("1");
});
