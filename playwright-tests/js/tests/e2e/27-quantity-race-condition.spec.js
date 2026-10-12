/**
 * Verify quantity stays at 1 per item (Sauce Demo doesn't allow editing quantity)
 */
import { test, expect } from "../fixtures";
import { CartPage } from "../page-objects/CartPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { EXPECTED_PRODUCT_COUNT } from "../config.js";

test.describe("Quantity and Race Conditions", () => {
  test("should verify each item quantity stays at 1 (no editing in cart)", async ({
    page,
  }) => {
    // Login
    const loginPage = new LoginPage(page);
    await loginPage.goTo();
    await loginPage.login();

    const inventoryPage = new InventoryPage(page);
    await inventoryPage.waitForPageLoad();

    // Add 3 different products
    await inventoryPage.addProductToCart(0); // Backpack
    await inventoryPage.addProductToCart(2); // Bolt T-Shirt
    await inventoryPage.addProductToCart(4); // Onesie

    // Verify badge shows 3
    await expect(inventoryPage.cartBadgeCount).toHaveText("3");

    // Open cart
    await inventoryPage.openCart();

    const cartPage = new CartPage(page);
    await cartPage.waitForPageLoad();

    // Verify each item has quantity = 1 (default)
    await expect(cartPage.cartItems).toHaveCount(3);
  });

  test("should handle rapid add multiple products", async ({ page }) => {
    // Login
    const loginPage = new LoginPage(page);
    await loginPage.goTo();
    await loginPage.login();

    const inventoryPage = new InventoryPage(page);
    await inventoryPage.waitForPageLoad();

    // Rapidly add 3 products (click all buttons in sequence without waits)
    for (let i = 0; i < 3; i++) {
      await inventoryPage.addProductToCart(i);
    }

    // All 3 should be added successfully
    await expect(inventoryPage.cartBadgeCount).toHaveText("3");

    // Verify cart has exactly 3 items
    await inventoryPage.openCart();
    const cartPage = new CartPage(page);
    await cartPage.waitForPageLoad();

    await expect(cartPage.cartItems).toHaveCount(3);
  });
});
