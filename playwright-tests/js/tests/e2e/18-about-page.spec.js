import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Тест 18: About page — переход из hamburger меню
 */
test("Тест 18: About page — переход из hamburger меню", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();

  // Открываем меню
  await loginPage.openHamburgerMenu();

  // Переходим по ссылке About
  await inventoryPage.clickAbout();

  // Закрываем меню только если оно открыто
  try {
    await loginPage.closeHamburgerMenu();
  } catch {
    // Меню уже закрылось при навигации
  }

  // Возвращаемся на Products
  await inventoryPage.goTo();
  await inventoryPage.waitForPageLoad();
  await expect(inventoryPage.headerTitle).toContainText("Products", {
    timeout: 5000,
  });
});
