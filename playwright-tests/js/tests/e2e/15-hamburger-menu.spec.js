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

  // Проверяем наличие элементов меню
  await expect(
    page.locator('[data-test="inventory-sidebar-link"]'),
  ).toBeVisible();
  await expect(page.locator('[data-test="about-sidebar-link"]')).toBeVisible();
  await expect(page.locator('[data-test="logout-sidebar-link"]')).toBeVisible();

  // Закрываем меню
  await loginPage.closeHamburgerMenu();
  await expect(loginPage.closeMenu).not.toBeVisible();
});
