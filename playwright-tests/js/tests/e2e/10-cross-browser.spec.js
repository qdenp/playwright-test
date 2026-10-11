import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Кросс-браузерная проверка — авторизация
 */
test("Кросс-браузерная проверка — авторизация", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();

  await expect(page).toHaveURL(/.*inventory\.html/);
  await inventoryPage.waitForPageLoad();

  const title = await inventoryPage.getTitleText();
  expect(title).toBe("Products");

  const productsVisible = await inventoryPage.isProductsVisible();
  expect(productsVisible).toBe(true);

  const productCount = await inventoryPage.getProductCount();
  expect(productCount).toBeGreaterThan(0);
});
