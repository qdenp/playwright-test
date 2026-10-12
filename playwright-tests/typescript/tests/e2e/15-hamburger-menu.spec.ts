import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Тест 15: Hamburger меню — открытие и закрытие
 */
test("Тест 15: Hamburger меню — открытие и закрытие", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();

  // Открываем меню
  await loginPage.openHamburgerMenu();
  await expect(loginPage.closeButton).toBeVisible();

  // Проверяем количество элементов меню (5 штук)
  await expect(inventoryPage.getMenuItemCount()).resolves.toBe(5);

  // Проверяем наличие элементов меню
  await expect(inventoryPage.isInventoryLinkVisible()).resolves.toBe(true);
  await expect(inventoryPage.isAboutLinkVisible()).resolves.toBe(true);
  await expect(inventoryPage.isLogoutLinkVisible()).resolves.toBe(true);

  // Закрываем меню
  await loginPage.closeHamburgerMenu();
  await expect(loginPage.closeButton).not.toBeVisible();
});
