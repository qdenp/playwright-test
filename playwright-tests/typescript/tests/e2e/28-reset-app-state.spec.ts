import { test, expect } from "../fixtures";
import { CartPage } from "../page-objects/CartPage.js";
import { timeouts } from "../config";

/**
 * Reset App State
 */
test.describe("Reset App State", () => {
  test("should reset cart via Reset App State in hamburger menu", async ({ authenticatedPage }) => {
    const { inventoryPage } = authenticatedPage;

    await inventoryPage.waitForPageLoad();

    // Add products and reset the cart via Reset App State
    for (let i = 0; i < 3; i++) {
      await inventoryPage.addProductToCart(i);
    }
    await expect(inventoryPage.cartBadgeCount).toHaveText("3");

    await inventoryPage.openHamburgerMenu();
    await inventoryPage.resetAppState();
    await inventoryPage.closeHamburgerMenu();

    // The cart is empty - no badge
    await expect(inventoryPage.cartBadgeCount).toHaveCount(0);
  });

  test("should reset app state and verify complete reset", async ({ authenticatedPage }) => {
    const { inventoryPage } = authenticatedPage;

    await inventoryPage.waitForPageLoad();

    for (let i = 0; i < 3; i++) {
      await inventoryPage.addProductToCart(i);
    }
    await expect(inventoryPage.cartBadgeCount).toHaveText("3");

    await inventoryPage.openHamburgerMenu();
    await inventoryPage.resetAppState();
    await inventoryPage.closeHamburgerMenu();

    await expect(inventoryPage.cartBadgeCount).toHaveCount(0);
  });
});
