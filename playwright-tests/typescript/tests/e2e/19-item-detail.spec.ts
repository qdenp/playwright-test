import { test, expect } from '../fixtures.js';
import { LoginPage } from '../page-objects/LoginPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';

/**
 * Тест 19: Item detail — просмотр и возврат
 */
test('Тест 19: Item detail — просмотр и возврат', async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Клик по названию первого товара — открывает detail panel
  await page.locator('[data-test="inventory-item-name"]').first().click();

  // Detail panel появляется inline — проверяем что description видно
  await expect(page.getByText('carry.allTheThings()')).toBeVisible({ timeout: 5000 });

  // Проверяем что есть кнопка "Back to Products"
  await expect(page.getByText('Back to Products')).toBeVisible({ timeout: 5000 });

  // Возвращаемся на Products
  await page.getByText('Back to Products').click();
  await expect(page.locator('[data-test="inventory-item-name"]').first()).toBeVisible({ timeout: 5000 });

  // Открываем детали второго товара
  await page.locator('[data-test="inventory-item-name"]').nth(1).click();

  // Проверяем detail второго товара
  await expect(page.getByText('carry.allTheThings()')).not.toBeVisible({ timeout: 5000 });
  await expect(page.getByText("A red light isn't the desired state")).toBeVisible({ timeout: 5000 });
  await expect(page.getByText('Back to Products')).toBeVisible({ timeout: 5000 });

  // Возвращаемся на Products
  await page.getByText('Back to Products').click();
  await expect(page.locator('[data-test="inventory-item-name"]').first()).toBeVisible({ timeout: 5000 });
});
