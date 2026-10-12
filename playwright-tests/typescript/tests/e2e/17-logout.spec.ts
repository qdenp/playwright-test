import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Тест 17: Logout — выход из аккаунта
 */
test("Тест 17: Logout — выход из аккаунта", async ({ authenticatedPage }) => {
  const { loginPage, inventoryPage } = authenticatedPage;
  const { page } = authenticatedPage;

  // Убедимся что мы на странице товаров
  await expect(page).toHaveURL(/.*inventory\.html/);

  // Открываем меню и нажимаем Logout
  await loginPage.openHamburgerMenu();
  await inventoryPage.clickLogout();

  // Проверяем что попали на страницу логина (SPA — URL не меняется, но появляются поля логина)
  await expect(loginPage.usernameInput).toBeVisible({ timeout: 10000 });
  await expect(loginPage.passwordInput).toBeVisible({ timeout: 10000 });
  await expect(loginPage.loginButton).toBeVisible({ timeout: 10000 });
});
