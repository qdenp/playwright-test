import { test as base, Page } from "@playwright/test";
import { LoginPage } from "./page-objects/LoginPage";
import { InventoryPage } from "./page-objects/InventoryPage";
import { CartPage } from "./page-objects/CartPage";
import { CheckoutPage } from "./page-objects/CheckoutPage";
import { ItemDetailPage } from "./page-objects/ItemDetailPage";
import { PRODUCTS, TEST_USERS } from "./config";

export { expect } from "@playwright/test";

/**
 * Custom fixture: a pre-authenticated page.
 * Used as: async ({ authenticatedPage }, test) => { ... }
 *
 * Not marked auto: login used to run in every test, even when
 * the test did not need it (a double login plus extra navigations).
 * Tests that only need a page without login use `page`.
 */
export const test = base.extend<{
  authenticatedPage: {
    page: Page;
    loginPage: LoginPage;
    inventoryPage: InventoryPage;
    cartPage: CartPage;
    checkoutPage: CheckoutPage;
    itemDetailPage: ItemDetailPage;
  };
}>({
  authenticatedPage: async ({ page }, use) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);
    const cartPage = new CartPage(page);
    const checkoutPage = new CheckoutPage(page);
    const itemDetailPage = new ItemDetailPage(page);

    // Navigate first, then pre-populate localStorage for GitHub Pages
    // (products are served from localStorage, not a server API)
    await loginPage.goTo();
    await page.evaluate(({ products, users }) => {
      const productData = JSON.stringify({ products });
      for (const user of users) {
        localStorage.setItem(`saucedemo_custom_user_${user}`, productData);
      }
    }, { products: PRODUCTS, users: TEST_USERS });
    // Reload to pick up the localStorage
    await page.reload({ waitUntil: "domcontentloaded" });

    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    await use({ page, loginPage, inventoryPage, cartPage, checkoutPage, itemDetailPage });
  },
});
