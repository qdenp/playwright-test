import { test, expect } from "../fixtures";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Test 05: Сортировка товаров по цене
 */
test.describe("Тест 5: Сортировка товаров по цене", () => {
  test("Тест 5a: Сортировка товаров по цене (low → high)", async ({ authenticatedPage }) => {
    const { inventoryPage } = authenticatedPage;

    // Sort by price low to high
    await inventoryPage.sortByPriceLowToHigh();

    // Verify prices are in ascending order
    const prices = await inventoryPage.getAllProductPrices();
    for (let i = 1; i < prices.length; i++) {
      expect(prices[i]).toBeGreaterThanOrEqual(prices[i - 1]);
    }
  });

  test("Тест 5b: Сортировка товаров по цене (high → low)", async ({ authenticatedPage }) => {
    const { inventoryPage } = authenticatedPage;

    // Sort by price high to low
    await inventoryPage.sortByPriceHighToLow();

    // Verify prices are in descending order
    const prices = await inventoryPage.getAllProductPrices();
    for (let i = 1; i < prices.length; i++) {
      expect(prices[i]).toBeLessThanOrEqual(prices[i - 1]);
    }
  });
});
