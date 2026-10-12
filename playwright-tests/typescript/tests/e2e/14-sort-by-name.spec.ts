import { test, expect } from "../fixtures.js";
import { EXPECTED_PRODUCT_COUNT } from "../config.js";

/**
 * Сортировка по имени (a→z)
 */
test("Сортировка товаров по имени (a→z)", async ({ authenticatedPage }) => {
  const { inventoryPage } = authenticatedPage;

  await inventoryPage.sortByAlphaAtoZ();

  // Список отрисован полностью — в нём 6 товаров
  await expect(inventoryPage.productNames).toHaveCount(EXPECTED_PRODUCT_COUNT);

  const names = await inventoryPage.getAllProductNames();

  for (let i = 1; i < names.length; i++) {
    expect(names[i].toLowerCase() > names[i - 1].toLowerCase()).toBeTruthy();
  }
});
