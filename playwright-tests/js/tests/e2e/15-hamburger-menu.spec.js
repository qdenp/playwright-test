import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";

/**
 * Тест 15: Hamburger меню — открытие и закрытие
 */
test("Тест 15: Hamburger меню — открытие и закрытие", async ({ page }) => {
  const loginPage = new LoginPage(page);

  await loginPage.goTo();
  await loginPage.login();

  // Открываем меню
  await loginPage.openHamburgerMenu();
  await expect(loginPage.closeMenu).toBeVisible();

  // Проверяем количество элементов меню (5 штук)
  await expect(loginPage.getMenuItemCount()).resolves.toBe(5);

  // Проверяем наличие элементов меню
  await expect(loginPage.isInventoryLinkVisible()).resolves.toBe(true);
  await expect(loginPage.isAboutLinkVisible()).resolves.toBe(true);
  await expect(loginPage.isLogoutLinkVisible()).resolves.toBe(true);

  // Закрываем меню
  await loginPage.closeHamburgerMenu();
  await expect(loginPage.closeMenu).not.toBeVisible();
});
