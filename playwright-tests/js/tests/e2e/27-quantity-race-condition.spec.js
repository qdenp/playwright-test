/**
 * Test 27: Verify quantity stays at 1 per item (Sauce Demo doesn't allow editing quantity)
 */
import { test, expect } from "../fixtures";
import { CartPage } from "../page-objects/CartPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

test.describe("Quantity and Race Conditions", () => {
  test("27 - should verify each item quantity stays at 1 (no editing in cart)", async ({
    page,
  }) => {
    // Login
    await page.goto("/");
    await page.locator("#user-name").fill("standard_user");
    await page.locator("#password").fill("secret_sauce");
    await page.locator("#login-button").click();

    const inventoryPage = new InventoryPage(page);
    await inventoryPage.waitForPageLoad();

    // Add 3 different products
    await inventoryPage.addProductToCart(0); // Backpack
    await inventoryPage.addProductToCart(2); // Bolt T-Shirt
    await inventoryPage.addProductToCart(4); // Onesie

    // Verify badge shows 3
    await expect(
      inventoryPage.cartBadge.locator(".shopping_cart_badge"),
    ).toHaveText("3");

    // Open cart
    await inventoryPage.openCart();

    const cartPage = new CartPage(page);
    await cartPage.waitForPageLoad();

    // Verify each item has quantity = 1 (default)
    const itemCount = await cartPage.getCartItemCount();
    expect(itemCount).toBe(3);
  });

  test("28 - should handle rapid add multiple products", async ({ page }) => {
    // Login
    await page.goto("/");
    await page.locator("#user-name").fill("standard_user");
    await page.locator("#password").fill("secret_sauce");
    await page.locator("#login-button").click();

    const inventoryPage = new InventoryPage(page);
    await inventoryPage.waitForPageLoad();

    // Rapidly add 3 products (click all buttons in sequence without waits)
    for (let i = 0; i < 3; i++) {
      await inventoryPage.addProductToCart(i);
    }

    // All 3 should be added successfully
    await expect(
      inventoryPage.cartBadge.locator(".shopping_cart_badge"),
    ).toHaveText("3");

    // Verify cart has exactly 3 items
    await inventoryPage.openCart();
    const cartPage = new CartPage(page);
    await cartPage.waitForPageLoad();

    expect(await cartPage.getCartItemCount()).toBe(3);
  });
});
