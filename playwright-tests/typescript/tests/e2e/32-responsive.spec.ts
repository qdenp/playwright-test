import { test, expect } from '../fixtures';
import { LoginPage } from '../page-objects/LoginPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';

/**
 * Test 32: Responsive - check mobile viewport
 */
test('32 - should work on mobile viewport', async ({ page }) => {
  await page.setViewportSize({ width: 375, height: 667 });

  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  const productCount = await inventoryPage.getProductCount();
  expect(productCount).toBe(6);

  await loginPage.openHamburgerMenu();
  await expect(loginPage.menuOverlay).toBeVisible();
  await loginPage.closeHamburgerMenu();
});
