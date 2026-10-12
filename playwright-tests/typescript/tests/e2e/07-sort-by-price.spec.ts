import { test, expect } from "../fixtures";

/**
 * Sorting products by price
 */
test.describe("Sorting products by price", () => {
  test("Sorting products by price (low to high)", async ({ authenticatedPage }) => {
    const { inventoryPage } = authenticatedPage;

    // Sort by price low to high
    await inventoryPage.sortByPriceLowToHigh();

    // Verify prices are in ascending order
    const prices = await inventoryPage.getAllProductPrices();
    for (let i = 1; i < prices.length; i++) {
      expect(prices[i]).toBeGreaterThanOrEqual(prices[i - 1]);
    }
    expect(prices[0]).toBe(Math.min(...prices));
  });

  test("Sorting products by price (high to low)", async ({ authenticatedPage }) => {
    const { inventoryPage } = authenticatedPage;

    // Sort by price high to low
    await inventoryPage.sortByPriceHighToLow();

    // Verify prices are in descending order
    const prices = await inventoryPage.getAllProductPrices();
    for (let i = 1; i < prices.length; i++) {
      expect(prices[i]).toBeLessThanOrEqual(prices[i - 1]);
    }
    expect(prices[0]).toBe(Math.max(...prices));
  });
});
