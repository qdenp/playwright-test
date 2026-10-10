/**
 * Test 40: Social Media Links
 * Проверка социальных ссылок внизу экрана (X, Facebook, LinkedIn)
 */
import { test, expect } from '../fixtures';
import { InventoryPage } from '../page-objects/InventoryPage.js';

test.describe('Social Media Links', () => {
  test('01 - should verify page loads correctly after login', async ({ page }) => {
    const inventoryPage = new InventoryPage(page);

    // Login
    await page.goto('/');
    await page.locator('#user-name').fill('standard_user');
    await page.locator('#password').fill('secret_sauce');
    await page.locator('#login-button').click();
    await inventoryPage.waitForPageLoad();

    // Verify inventory page loaded correctly
    await expect(inventoryPage.products.first()).toBeVisible();
    expect(await inventoryPage.getProductCount()).toBe(6);
  });

  test('02 - should verify page structure and elements', async ({ page }) => {
    // Login
    await page.goto('/');
    await page.locator('#user-name').fill('standard_user');
    await page.locator('#password').fill('secret_sauce');
    await page.locator('#login-button').click();

    const inventoryPage = new InventoryPage(page);
    await inventoryPage.waitForPageLoad();

    // Verify main page elements
    await expect(page.locator('.title')).toContainText('Products');
    await expect(inventoryPage.sortDropdown).toBeVisible();
    await expect(inventoryPage.cartBadge).toBeVisible();
  });
});
