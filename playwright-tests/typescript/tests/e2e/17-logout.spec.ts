import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Тест 17: Logout — выход из аккаунта
 */
test("Тест 17: Logout — выход из аккаунта", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Убедимся что мы на странице товаров
  await expect(page).toHaveURL(/.*inventory\.html/);

  // Открываем меню и нажимаем Logout
  await loginPage.openHamburgerMenu();
  await loginPage.clickLogout();

  // Проверяем что попали на страницу логина (SPA — URL не меняется, но появляются поля логина)
  await expect(loginPage.usernameInput).toBeVisible({ timeout: 10000 });
  await expect(loginPage.passwordInput).toBeVisible({ timeout: 10000 });
  await expect(loginPage.loginButton).toBeVisible({ timeout: 10000 });
});
