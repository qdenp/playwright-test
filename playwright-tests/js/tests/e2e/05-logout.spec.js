import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Logout - signing out of the account
 */
test("Logout - signing out of the account", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Ensure that we are on the inventory page
  await expect(page).toHaveURL(/.*inventory\.html/);

  // Open the menu and click Logout
  await loginPage.openHamburgerMenu();
  await inventoryPage.clickLogout();

  // Verify we are back on the login page (SPA - the URL does not change but the login fields appear)
  await expect(loginPage.usernameInput).toBeVisible({ timeout: 10000 });
  await expect(loginPage.passwordInput).toBeVisible({ timeout: 10000 });
  await expect(loginPage.loginButton).toBeVisible({ timeout: 10000 });
});
