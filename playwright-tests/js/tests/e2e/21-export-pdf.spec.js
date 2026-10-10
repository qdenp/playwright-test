import { test, expect } from '../fixtures.js';
import { LoginPage } from '../page-objects/LoginPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';
import { CartPage } from '../page-objects/CartPage.js';
import { CheckoutPage } from '../page-objects/CheckoutPage.js';
import fs from 'fs';

/**
 * Тест 21: Экспорт PDF заказа
 */
test('Тест 21: Экспорт PDF заказа', async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);
  const cartPage = new CartPage(page);
  const checkoutPage = new CheckoutPage(page);

  // Добавляем товары в корзину
  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  await inventoryPage.addProductToCart(0);
  await inventoryPage.addProductToCart(1);
  await expect(inventoryPage.getCartBadgeCount()).resolves.toBe(2);

  // Переходим к оформлению
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  await cartPage.checkout();
  await checkoutPage.waitForPageLoad();

  await checkoutPage.fillInfo('Иван', 'Иванов', '100000');
  await checkoutPage.clickContinue();

  // Ожидаем перехода на checkout-step-two (Firefox может работать медленнее)
  await page.waitForURL(/.*checkout-step-two\.html/, { timeout: 15000 });

  // Нажимаем Finish
  await checkoutPage.clickFinish();

  // Ждём страницу подтверждения
  await page.waitForURL(/.*checkout-complete\.html/, { timeout: 10000 });

  // Проверяем что кнопка Generate PDF видна
  await expect(page.getByRole('button', { name: 'Generate PDF' })).toBeVisible({ timeout: 5000 });

  // Кликаем Generate PDF и ждём скачивания
  const [download] = await Promise.all([
    page.waitForEvent('download'),
    page.getByRole('button', { name: 'Generate PDF' }).click(),
  ]);

  // Сохраняем PDF
  const pdfPath = await download.path();
  expect(pdfPath).toBeTruthy();

  // Проверяем что файл существует и не пустой
  const stats = fs.statSync(pdfPath);
  expect(stats.size).toBeGreaterThan(0);
});
