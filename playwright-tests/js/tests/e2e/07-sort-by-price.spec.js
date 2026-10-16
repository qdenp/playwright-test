import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage";
import { InventoryPage } from "../page-objects/InventoryPage";

/**
 * Sorting products by price (low to high)
 */
test("Sorting products by price (low to high)", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  await inventoryPage.sortByPriceLowToHigh();

  const prices = await inventoryPage.getAllProductPrices();

  for (let i = 1; i < prices.length; i++) {
    expect(prices[i]).toBeGreaterThanOrEqual(prices[i - 1]);
  }
  expect(prices[0]).toBe(Math.min(...prices));
});

/**
 * Sorting products by price (high to low)
 */
test("Sorting products by price (high to low)", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  await inventoryPage.sortByPriceHighToLow();

  const prices = await inventoryPage.getAllProductPrices();

  for (let i = 1; i < prices.length; i++) {
    expect(prices[i]).toBeLessThanOrEqual(prices[i - 1]);
  }
  expect(prices[0]).toBe(Math.max(...prices));
});
