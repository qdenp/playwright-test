import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";

/**
 * Тест 16: Визуальный скриншот страницы товаров
 */
test("Тест 16: Визуальный скриншот — страница товаров", async ({ page }) => {
  const loginPage = new LoginPage(page);

  await loginPage.goTo();
  await loginPage.login();

  await expect(page).toHaveURL(/.*inventory\.html/);

  // Делаем скриншот страницы товаров
  await expect(page.locator(".title")).toBeVisible({ timeout: 10000 });
  await page.screenshot({
    path: "test-results/screenshots/products-page.png",
    fullPage: false,
  });
});
