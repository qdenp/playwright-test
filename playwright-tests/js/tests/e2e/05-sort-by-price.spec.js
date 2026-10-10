import { test, expect } from '../fixtures.js';
import { LoginPage } from '../page-objects/LoginPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';

/**
 * Тест 5a: Сортировка товаров по цене (low → high)
 */
test('Тест 5a: Сортировка товаров по цене (low → high)', async ({ page }) => {
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
 * Тест 5b: Сортировка товаров по цене (high → low)
 */
test('Тест 5b: Сортировка товаров по цене (high → low)', async ({ page }) => {
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
