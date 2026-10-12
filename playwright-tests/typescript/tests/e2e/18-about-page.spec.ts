import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Тест 18: About page — переход из hamburger меню
 */
test("Тест 18: About page — переход из hamburger меню", { timeout: 60000 }, async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();

  // Открываем меню
  await loginPage.openHamburgerMenu();

  // Переходим по ссылке About
  await inventoryPage.clickAbout();

  // About может перейти на saucelabs.com — возвращаемся
  await page.bringToFront();
  await page.goto("/inventory.html", { timeout: 15000 });
  await inventoryPage.waitForPageLoad();
  await expect(inventoryPage.headerTitle).toContainText("Products", {
    timeout: 5000,
  });
});
