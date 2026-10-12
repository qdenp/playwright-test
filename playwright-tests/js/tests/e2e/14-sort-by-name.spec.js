import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { EXPECTED_PRODUCT_COUNT } from "../config.js";

/**
 * Сортировка по имени (a→z)
 */
test("Сортировка товаров по имени (a→z)", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  await inventoryPage.sortByAlphaAtoZ();

  // Список отрисован полностью — в нём 6 товаров
  await expect(inventoryPage.productNames).toHaveCount(EXPECTED_PRODUCT_COUNT);

  const names = await inventoryPage.getAllProductNames();

  for (let i = 1; i < names.length; i++) {
    expect(names[i].toLowerCase() > names[i - 1].toLowerCase()).toBeTruthy();
  }
});
