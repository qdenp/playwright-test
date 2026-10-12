import { test as base } from "@playwright/test";
import { LoginPage } from "./page-objects/LoginPage.js";
import { InventoryPage } from "./page-objects/InventoryPage.js";
import { config } from "./config.js";

export { expect } from "@playwright/test";

/**
 * Custom fixture: an automatically authenticated page
 * Used as: async ({ authenticatedPage }, test) => { ... }
 */
export const test = base.extend({
  authenticatedPage: async ({ page }, use) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login(config.username, config.password);
    await inventoryPage.waitForPageLoad();

    await use({ page, loginPage, inventoryPage });
  },
});
