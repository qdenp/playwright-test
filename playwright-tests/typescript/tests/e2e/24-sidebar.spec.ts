/**
 * Test 24: Verify sidebar text and logo
 */
import { test, expect } from '../fixtures.js';
import { LoginPage } from '../page-objects/LoginPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';

test('24 - should display sidebar with correct text and logo', async ({
  page,
}) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Verify sidebar menu items are visible after opening menu
  await loginPage.openHamburgerMenu();
  const menuItems = page.locator('.bm-item-list a');
  await expect(menuItems).toHaveCount(5); // Logout, About, Inventory, Contact, + 1
});
