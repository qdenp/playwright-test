import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Тест 16: Визуальный скриншот страницы товаров
 */
test("Тест 16: Визуальный скриншот — страница товаров", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();

  await expect(page).toHaveURL(/.*inventory\.html/);

  // Ждём загрузки страницы товаров
  await inventoryPage.waitForPageLoad();

  // Делаем скриншот страницы товаров
  await page.screenshot({
    path: "test-results/screenshots/products-page.png",
    fullPage: false,
  });
});
