import { test as base } from "@playwright/test";
import { LoginPage } from "./page-objects/LoginPage";
import { InventoryPage } from "./page-objects/InventoryPage";
import { CartPage } from "./page-objects/CartPage";
import { CheckoutPage } from "./page-objects/CheckoutPage";
import { ItemDetailPage } from "./page-objects/ItemDetailPage";
import { config } from "./config";

export { expect } from "@playwright/test";

/**
 * Custom fixture: an automatically authenticated page
 * Used as: async ({ authenticatedPage }, test) => { ... }
 */
export const test = base.extend({
  authenticatedPage: async ({ page }, use) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);
    const cartPage = new CartPage(page);
    const checkoutPage = new CheckoutPage(page);
    const itemDetailPage = new ItemDetailPage(page);

    await loginPage.goTo();
    await loginPage.login(config.username, config.password);
    await inventoryPage.waitForPageLoad();

    await use({ page, loginPage, inventoryPage, cartPage, checkoutPage, itemDetailPage });
  },
});
