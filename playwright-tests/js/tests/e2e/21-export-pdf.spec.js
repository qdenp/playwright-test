import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";
import { CheckoutPage } from "../page-objects/CheckoutPage.js";
import { timeouts } from "../config.js";
import fs from "fs";

/**
 * Тест 21: Экспорт PDF заказа
 */
test("Тест 21: Экспорт PDF заказа", async ({ page }) => {
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
  await expect(inventoryPage.cartBadgeCount).toHaveText("2");

  // Переходим к оформлению
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  await cartPage.checkout();
  await checkoutPage.waitForPageLoad();

  await checkoutPage.fillInfo("Иван", "Иванов", "100000");
  await checkoutPage.clickContinue();

  // Ожидаем перехода на checkout-step-two (Firefox может работать медленнее)
  await expect(checkoutPage.finishButton).toBeVisible({
    timeout: timeouts.NAVIGATION,
  });

  // Нажимам Finish
  await checkoutPage.clickFinish();

  // Ждём страницу подтверждения
  await expect(checkoutPage.completeTitle).toBeVisible({
    timeout: timeouts.NAVIGATION,
  });

  // Кликаем Generate PDF и ждём скачивания
  const download = await checkoutPage.generatePdf();

  // Сохраняем PDF
  const pdfPath = await download.path();
  expect(pdfPath).toBeTruthy();

  // Проверяем что файл существует и не пустой
  const stats = fs.statSync(pdfPath);
  expect(stats.size).toBeGreaterThan(0);
});
