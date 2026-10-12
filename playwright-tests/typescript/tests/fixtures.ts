import { test as base, Page } from "@playwright/test";
import { LoginPage } from "./page-objects/LoginPage.js";
import { InventoryPage } from "./page-objects/InventoryPage.js";
import { CartPage } from "./page-objects/CartPage.js";
import { CheckoutPage } from "./page-objects/CheckoutPage.js";

export { expect } from "@playwright/test";

/**
 * Custom fixture: предварительно авторизованная страница.
 * Используется через: async ({ authenticatedPage }, test) => { ... }
 *
 * НЕ помечен как auto: ранее логин выполнялся в каждом тесте, даже когда
 * тест в нём не нуждался (двойной логин + лишние переходы).
 * Тесты, которым нужна только страница без логина, используют `page`.
 */
export const test = base.extend<{
  authenticatedPage: {
    page: Page;
    loginPage: LoginPage;
    inventoryPage: InventoryPage;
    cartPage: CartPage;
    checkoutPage: CheckoutPage;
  };
}>({
  authenticatedPage: async ({ page }, use) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);
    const cartPage = new CartPage(page);
    const checkoutPage = new CheckoutPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    await use({ page, loginPage, inventoryPage, cartPage, checkoutPage });
  },
});
