import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Test 05: Сортировка товаров по цене
 */
test.describe("Sort by Price", () => {
  test("05a - should sort products by price low to high", async ({ page }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    // Sort by price low to high
    await inventoryPage.sortByPriceLowToHigh();

    // Verify prices are in ascending order
    const prices = await inventoryPage.getAllProductPrices();
    for (let i = 1; i < prices.length; i++) {
      expect(prices[i]).toBeGreaterThanOrEqual(prices[i - 1]);
    }
  });

  test("05b - should sort products by price high to low", async ({ page }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    // Sort by price high to low
    await inventoryPage.sortByPriceHighToLow();

    // Verify prices are in descending order
    const prices = await inventoryPage.getAllProductPrices();
    for (let i = 1; i < prices.length; i++) {
      expect(prices[i]).toBeLessThanOrEqual(prices[i - 1]);
    }
  });
});
