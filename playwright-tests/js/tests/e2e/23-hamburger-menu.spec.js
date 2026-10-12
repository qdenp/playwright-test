import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Hamburger menu - opening and closing
 */
test("Hamburger menu - opening and closing", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();

  // Open the menu
  await loginPage.openHamburgerMenu();
  await expect(loginPage.closeMenu).toBeVisible();

  // Verify the menu has 5 items
  await expect(inventoryPage.getMenuItemsLocator()).toHaveCount(5);

  // Verify the menu items are present
  await expect(inventoryPage.inventoryLink).toBeVisible();
  await expect(inventoryPage.aboutLink).toBeVisible();
  await expect(inventoryPage.logoutLink).toBeVisible();

  // Close the menu
  await loginPage.closeHamburgerMenu();
  await expect(loginPage.closeMenu).not.toBeVisible();
});
