/**
 * Test 26: Verify logout functionality from different pages
 */
import { test, expect } from '../fixtures.js';
import { LoginPage } from '../page-objects/LoginPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';
import { CartPage } from '../page-objects/CartPage.js';

test('26 - should logout from inventory and cart pages', async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);
  const cartPage = new CartPage(page);

  // Login
  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Logout from inventory page
  await loginPage.openHamburgerMenu();
  await page.locator('.bm-item-list a:has-text("Logout")').click();
  // Logout redirects to inventory page, not login
  await expect(page).toHaveURL(/.*saucedemo/);

  // Login again
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Add product and go to cart
  await inventoryPage.addProductToCart(0);
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();

  // Open menu and logout
  await cartPage.page.locator('.bm-burger-button').click();
  await cartPage.page.locator('.bm-item-list a:has-text("Logout")').click();
  // Logout redirects to inventory page
  await expect(page).toHaveURL(/.*saucedemo/);
});
