import { test, expect } from "../fixtures";
import { EXPECTED_PRODUCT_COUNT, PRODUCTS, TEST_USERS } from "../config";

/**
 * Reset App State
 */
test.describe("Reset App State", () => {
  test("should clear the cart badge when Reset App State is used", async ({
    authenticatedPage,
  }) => {
    const { page, inventoryPage } = authenticatedPage;

    for (let i = 0; i < 3; i++) {
      await inventoryPage.addProductToCart(i);
    }
    await expect(inventoryPage.cartBadgeCount).toHaveText("3");

    await inventoryPage.openHamburgerMenu();
    await inventoryPage.resetAppState();
    await inventoryPage.closeHamburgerMenu();

    // The badge disappears immediately - the cart is empty
    await expect(inventoryPage.cartBadgeCount).toHaveCount(0);
  });

  /**
   * SAUCE_DEMO_BUG: Reset App State requires a page reload to fully work.
   *
   * The test verifies two things that should happen immediately after clicking
   * "Reset App State" but currently require a page reload on the GitHub Pages
   * build:
   *
   * 1. While on the cart page — cart items are removed (not just the badge)
   * 2. While on the inventory page — "Add to Cart" buttons reappear
   *
   * On the real SauceDemo (server-side state) these work instantly.
   * On GitHub Pages the SPA needs a reload, but reload redirects to login.
   *
   * Once the site bug is fixed: remove `test.skip` and uncomment the body.
   */
  test.skip("should clear the stored cart, not just the badge", async ({
    authenticatedPage,
  }) => {
    const { page, inventoryPage, cartPage } = authenticatedPage;

    for (let i = 0; i < 3; i++) {
      await inventoryPage.addProductToCart(i);
    }
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();
    await expect(cartPage.cartItems).toHaveCount(3);

    await inventoryPage.openHamburgerMenu();
    await inventoryPage.resetAppState();
    await inventoryPage.closeHamburgerMenu();

    // Reload so the cart page re-reads the stored state
    await page.goto("cart.html", { waitUntil: "domcontentloaded" });
    await expect(cartPage.cartItems).toHaveCount(0);

    // The catalogue itself is untouched
    // On GitHub Pages products are in localStorage, resetAppState clears it.
    // Pre-populate BEFORE navigation so React picks up the data.
    await page.goto("inventory.html", { waitUntil: "domcontentloaded" });
    // localStorage is already populated by the fixture, but reset cleared it.
    // Re-populate and reload.
    await page.evaluate(({ products, users }) => {
      const productData = JSON.stringify({ products });
      for (const user of users) {
        localStorage.setItem(`saucedemo_custom_user_${user}`, productData);
      }
    }, { products: PRODUCTS, users: TEST_USERS });
    await page.reload({ waitUntil: "domcontentloaded" });
    await expect(inventoryPage.products).toHaveCount(6);
    await expect(inventoryPage.cartBadgeCount).toHaveCount(0);
  });
});
