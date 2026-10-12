import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Тест 19: Item detail — просмотр и возврат
 */
test("Тест 19: Item detail — просмотр и возврат", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Клик по названию первого товара — открывает detail panel
  await inventoryPage.clickProductName(0);

  // Detail panel появляется inline — проверяем что description видно
  await expect(inventoryPage.getDetailDescriptionLocator("carry.allTheThings()")).toBeVisible({
    timeout: 5000,
  });

  // Проверяем что есть кнопка "Back to Products"
  await expect(inventoryPage.getBackToProductsLocator()).toBeVisible({
    timeout: 5000,
  });

  // Возвращаемся на Products
  await inventoryPage.clickBackToProducts();
  await expect(inventoryPage.getProductNameLocator(0)).toBeVisible({
    timeout: 5000,
  });

  // Открываем детали второго товара
  await inventoryPage.clickProductName(1);

  // Проверяем detail второго товара
  await expect(inventoryPage.getDetailDescriptionLocator("carry.allTheThings()")).not.toBeVisible({
    timeout: 5000,
  });
  await expect(inventoryPage.getBackToProductsLocator()).toBeVisible({
    timeout: 5000,
  });

  // Возвращаемся на Products
  await inventoryPage.clickBackToProducts();
  await expect(inventoryPage.getProductNameLocator(0)).toBeVisible({
    timeout: 5000,
  });
});
