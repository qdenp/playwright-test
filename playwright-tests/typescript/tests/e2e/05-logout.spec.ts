import { test, expect } from "../fixtures";
import { timeouts } from "../config";

/**
 * Logout - signing out of the account
 */
test("Logout - signing out of the account", async ({ authenticatedPage }) => {
  const { page, inventoryPage } = authenticatedPage;

  // Ensure that we are on the inventory page
  await expect(page).toHaveURL(/.*inventory\.html/);

  // Open the menu and click Logout
  await inventoryPage.openHamburgerMenu();
  await inventoryPage.clickLogout();

  // Logging out brings the login form back
  await expect(page.locator("#user-name")).toBeVisible({
    timeout: timeouts.DEFAULT,
  });
  await expect(page.locator("#password")).toBeVisible({
    timeout: timeouts.DEFAULT,
  });
  await expect(page.locator("#login-button")).toBeVisible({
    timeout: timeouts.DEFAULT,
  });

  // The inventory page is not reachable any more
  await expect(inventoryPage.headerTitle).toBeHidden();
});
