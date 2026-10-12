/**
 * Reset App State
 */
import { test, expect } from "../fixtures";
import { EXPECTED_PRODUCT_COUNT } from "../config.js";
import { CartPage } from "../page-objects/CartPage.js";

test.describe("Reset App State", () => {
  test("should clear the cart badge when Reset App State is used", async ({
    authenticatedPage,
  }) => {
    const { inventoryPage } = authenticatedPage;

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

  test("should clear the stored cart, not just the badge", async ({
    authenticatedPage,
  }) => {
    const { page, inventoryPage } = authenticatedPage;
    const cartPage = new CartPage(page);

    for (let i = 0; i < 3; i++) {
      await inventoryPage.addProductToCart(i);
    }
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();
    await expect(cartPage.cartItems).toHaveCount(3);

    await inventoryPage.openHamburgerMenu();
    await inventoryPage.resetAppState();
    await inventoryPage.closeHamburgerMenu();

    // Re-open the cart so the page re-reads the stored state
    await page.goto("/cart.html", { waitUntil: "domcontentloaded" });
    await expect(cartPage.cartItems).toHaveCount(0);

    // The catalogue itself is untouched
    await page.goto("/inventory.html", { waitUntil: "domcontentloaded" });
    await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);
    await expect(inventoryPage.cartBadgeCount).toHaveCount(0);
  });
});
