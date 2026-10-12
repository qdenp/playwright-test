import { test, expect } from "../fixtures.js";
import { timeouts } from "../config.js";

/**
 * Logout - signing out of the account
 */
test("Logout - signing out of the account", async ({ authenticatedPage }) => {
  const { loginPage, inventoryPage } = authenticatedPage;
  const { page } = authenticatedPage;

  // Ensure that we are on the inventory page
  await expect(page).toHaveURL(/.*inventory\.html/);

  // Open the menu and click Logout
  await loginPage.openHamburgerMenu();
  await inventoryPage.clickLogout();

  // Logging out brings the login form back
  await expect(loginPage.usernameInput).toBeVisible({
    timeout: timeouts.DEFAULT,
  });
  await expect(loginPage.passwordInput).toBeVisible({
    timeout: timeouts.DEFAULT,
  });
  await expect(loginPage.loginButton).toBeVisible({
    timeout: timeouts.DEFAULT,
  });

  // The inventory page is not reachable any more
  await expect(inventoryPage.headerTitle).toBeHidden();
});
