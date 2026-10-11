import { test, expect } from '../fixtures';
import { LoginPage } from '../page-objects/LoginPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';

/**
 * Test 39: Performance - verify page load times
 */
test.describe('Performance Tests', () => {
  test('01 - should load products page quickly', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);
    
    const startTime = Date.now();
    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();
    const loadTime = Date.now() - startTime;

    expect(loadTime).toBeLessThan(10000);
  });

  test('02 - should add products to cart quickly', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    const startTime = Date.now();
    for (let i = 0; i < 3; i++) {
      await inventoryPage.addProductToCart(i);
    }
    const addTime = Date.now() - startTime;

    expect(addTime).toBeLessThan(5000);
    expect(await inventoryPage.getCartBadgeCount()).toBe(3);
  });
});
