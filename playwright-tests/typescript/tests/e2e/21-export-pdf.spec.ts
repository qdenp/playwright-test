import { test, expect } from "../fixtures.js";
import { CartPage } from "../page-objects/CartPage.js";
import { CheckoutPage } from "../page-objects/CheckoutPage.js";
import { timeouts } from "../config";
import fs from "fs";

/**
 * Тест 21: Экспорт PDF заказа
 */
test("Тест 21: Экспорт PDF заказа", async ({ authenticatedPage }) => {
  const { inventoryPage, cartPage, checkoutPage } = authenticatedPage;

  // Добавляем товары в корзину
  await inventoryPage.addProductToCart(0);
  await inventoryPage.addProductToCart(1);
  await expect(inventoryPage.cartBadgeCount).toHaveText("2");

  // Переходим к оформлению
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  await cartPage.checkout();
  await checkoutPage.waitForPageLoad();

  await checkoutPage.fillFirstName("Иван");
  await checkoutPage.fillLastName("Иванов");
  await checkoutPage.fillZipCode("100000");
  await checkoutPage.clickContinue();

  // Ожидаем перехода на checkout-step-two (Firefox может работать медленнее)
  await expect(checkoutPage.finishButton).toBeVisible({
    timeout: timeouts.NAVIGATION,
  });

  // Нажима Finish
  await checkoutPage.clickFinish();

  // Ждём страницу подтверждения
  await expect(checkoutPage.completeTitle).toBeVisible({
    timeout: timeouts.NAVIGATION,
  });

  // Кликаем Generate PDF и ждём скачивания
  const download = await checkoutPage.generatePdf();

  // Проверяем что файл не пустой
  const pdfPath = await download.path();
  expect(pdfPath).toBeTruthy();
  const stats = fs.statSync(pdfPath);
  expect(stats.size).toBeGreaterThan(0);
});
