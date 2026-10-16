/**
 * Verify logout functionality from different pages
 */
import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage";
import { InventoryPage } from "../page-objects/InventoryPage";
import { CartPage } from "../page-objects/CartPage";
import { timeouts } from "../config";

test('should logout from the inventory page', async ({ authenticatedPage }) => {
  const { page, loginPage, inventoryPage } = authenticatedPage;

  await inventoryPage.openHamburgerMenu();
  await inventoryPage.clickLogout();

  // Logout really happened: the login form is back and the inventory is gone
  await expect(loginPage.usernameInput).toBeVisible({ timeout: timeouts.DEFAULT });
  await expect(loginPage.loginButton).toBeVisible();
  await expect(inventoryPage.headerTitle).toBeHidden();

  // The inventory page is no longer reachable without logging in again
  await page.goto('inventory.html', { waitUntil: 'domcontentloaded' });
  await expect(loginPage.usernameInput).toBeVisible();
});

test('should logout from the cart page', async ({ authenticatedPage }) => {
  const { page, loginPage, inventoryPage, cartPage } = authenticatedPage;

  await inventoryPage.addProductToCart(0);
  await expect(inventoryPage.cartBadgeCount).toHaveText('1');

  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();

  await inventoryPage.openHamburgerMenu();
  await inventoryPage.clickLogout();

  await expect(loginPage.usernameInput).toBeVisible({ timeout: timeouts.DEFAULT });
  await expect(loginPage.loginButton).toBeVisible();

  // Logging back in returns to the inventory page
  await loginPage.login();
  await inventoryPage.waitForPageLoad();
  await expect(page).toHaveURL(/.*inventory\.html/);
});
