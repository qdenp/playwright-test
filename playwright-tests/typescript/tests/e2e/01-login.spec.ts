import { test, expect } from '../fixtures';
import { LoginPage } from '../page-objects/LoginPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';

/**
 * Test 01: Успешный логин — стандартный пользователь
 */
test('01 - should login successfully with standard_user', async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Verify we're on the products page
  await expect(inventoryPage.headerTitle).toBeVisible();
  expect(await inventoryPage.getProductCount()).toBe(6);
});
