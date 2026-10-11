import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";
import { CheckoutPage } from "../page-objects/CheckoutPage.js";

/**
 * Валидация формы checkout — пустые поля
 */
test("Checkout валидация — пустые поля", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);
  const cartPage = new CartPage(page);
  const checkoutPage = new CheckoutPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  await inventoryPage.addProductToCart(0);
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();

  await cartPage.checkout();
  await checkoutPage.waitForPageLoad();

  // Нажимаем Continue без заполнения полей
  await checkoutPage.clickContinue();

  // Ожидаем ошибки валидации для всех полей
  await expect(checkoutPage.firstNameInput).toBeVisible();
  await expect(checkoutPage.lastNameInput).toBeVisible();
  await expect(checkoutPage.zipInput).toBeVisible();

  const errorMessage = await checkoutPage.getErrorMessage();
  expect(errorMessage).toBeTruthy();
});
