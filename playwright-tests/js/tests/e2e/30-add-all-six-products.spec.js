/**
 * Test 30: Add all 6 products and verify
 */
import { test, expect } from "../fixtures";
import { CartPage } from "../page-objects/CartPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

test.describe("Add All 6 Products", () => {
  test("01 - should add all 6 products and verify cart", async ({ page }) => {
    // Login
    await page.goto("/");
    await page.locator("#user-name").fill("standard_user");
    await page.locator("#password").fill("secret_sauce");
    await page.locator("#login-button").click();

    const inventoryPage = new InventoryPage(page);
    await inventoryPage.waitForPageLoad();

    const cartPage = new CartPage(page);

    // Add all 6 products (indices 0-5)
    for (let i = 0; i < 6; i++) {
      await inventoryPage.addProductToCart(i);
    }

    // Verify cart badge shows 6
    await expect(
      inventoryPage.cartBadge.locator(".shopping_cart_badge"),
    ).toHaveText("6");

    // Open cart
    await inventoryPage.openCart();

    // Verify all 6 items in cart
    await cartPage.waitForPageLoad();
    expect(await cartPage.getCartItemCount()).toBe(6);
  });

  test("02 - should verify product count and prices", async ({ page }) => {
    // Login
    await page.goto("/");
    await page.locator("#user-name").fill("standard_user");
    await page.locator("#password").fill("secret_sauce");
    await page.locator("#login-button").click();

    const inventoryPage = new InventoryPage(page);
    await inventoryPage.waitForPageLoad();

    // Verify 6 products loaded
    const productCount = await inventoryPage.getProductCount();
    expect(productCount).toBe(6);

    // Verify all prices are valid numbers > 0
    for (let i = 0; i < productCount; i++) {
      const price = await inventoryPage.getProductPrice(i);
      expect(price).toBeGreaterThan(0);
      expect(price).toBeLessThanOrEqual(100);
    }
  });

  test("03 - should add all 6 products and verify product names", async ({
    page,
  }) => {
    // Login
    await page.goto("/");
    await page.locator("#user-name").fill("standard_user");
    await page.locator("#password").fill("secret_sauce");
    await page.locator("#login-button").click();

    const inventoryPage = new InventoryPage(page);
    await inventoryPage.waitForPageLoad();

    const cartPage = new CartPage(page);

    // Add all 6 products
    for (let i = 0; i < 6; i++) {
      await inventoryPage.addProductToCart(i);
    }

    // Open cart
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();

    // Verify specific product names
    const expectedNames = [
      "Sauce Labs Backpack",
      "Sauce Labs Bike Light",
      "Sauce Labs Bolt T-Shirt",
      "Sauce Labs Fleece Jacket",
      "Sauce Labs Onesie",
      "Test.allTheThings() T-Shirt (Red)",
    ];

    const cartItems = await cartPage.getCartItemNames();
    expect(cartItems).toHaveLength(6);

    for (const name of expectedNames) {
      expect(cartItems).toContain(name);
    }
  });
});
