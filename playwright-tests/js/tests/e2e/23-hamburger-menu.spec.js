import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Hamburger меню — открытие и закрытие
 */
test("Hamburger меню — открытие и закрытие", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();

  // Открываем меню
  await loginPage.openHamburgerMenu();
  await expect(loginPage.closeMenu).toBeVisible();

  // Проверяем количество элементов меню (5 штук)
  await expect(inventoryPage.getMenuItemsLocator()).toHaveCount(5);

  // Проверяем наличие элементов меню
  await expect(inventoryPage.inventoryLink).toBeVisible();
  await expect(inventoryPage.aboutLink).toBeVisible();
  await expect(inventoryPage.logoutLink).toBeVisible();

  // Закрываем меню
  await loginPage.closeHamburgerMenu();
  await expect(loginPage.closeMenu).not.toBeVisible();
});
