import { test, expect } from "../fixtures";

/**
 * Hamburger menu - opening and closing
 */
test("Hamburger menu - opening and closing", async ({ authenticatedPage }) => {
  const { loginPage, inventoryPage } = authenticatedPage;

  // Open the menu
  await loginPage.openHamburgerMenu();
  await expect(loginPage.closeButton).toBeVisible();

  // Verify the menu has 5 items
  await expect(inventoryPage.getMenuItemsLocator()).toHaveCount(5);

  // Verify the menu items are present
  await expect(inventoryPage.inventoryLink).toBeVisible();
  await expect(inventoryPage.aboutLink).toBeVisible();
  await expect(inventoryPage.logoutLink).toBeVisible();

  // Close the menu
  await loginPage.closeHamburgerMenu();
  await expect(loginPage.closeButton).not.toBeVisible();
});
