import { test, expect } from "../fixtures.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Тест 16: Визуальный скриншот страницы товаров
 */
test("Тест 16: Визуальный скриншот — страница товаров", async ({ authenticatedPage }) => {
  const { page, inventoryPage } = authenticatedPage;

  await expect(page).toHaveURL(/.*inventory\.html/);

  // Ждём загрузки страницы товаров
  await inventoryPage.waitForPageLoad();

  // Делаем скриншот страницы товаров
  await page.screenshot({
    path: "test-results/screenshots/products-page.png",
    fullPage: false,
  });
});
