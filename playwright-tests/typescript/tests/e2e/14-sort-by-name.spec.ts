import { test, expect } from "../fixtures.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Тест 14: Сортировка по имени (a→z)
 */
test("Тест 14: Сортировка товаров по имени (a→z)", async ({ authenticatedPage }) => {
  const { inventoryPage } = authenticatedPage;

  await inventoryPage.sortByAlphaAtoZ();

  const names = await inventoryPage.getAllProductNames();

  for (let i = 1; i < names.length; i++) {
    expect(names[i].toLowerCase() > names[i - 1].toLowerCase()).toBeTruthy();
  }
});
