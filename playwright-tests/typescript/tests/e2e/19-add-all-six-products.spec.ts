import { test, expect } from "../fixtures";
import { CartPage } from "../page-objects/CartPage.js";
import { EXPECTED_PRODUCT_COUNT } from "../config";

/**
 * Add all 6 products and verify
 */
test.describe("Add All 6 Products", () => {
  test("should add all 6 products and verify cart", async ({ authenticatedPage }) => {
    const { inventoryPage, cartPage } = authenticatedPage;

    // Add all 6 products (indices 0-5)
    for (let i = 0; i < EXPECTED_PRODUCT_COUNT; i++) {
      await inventoryPage.addProductToCart(i);
    }

    // Verify cart badge shows 6
    await expect(inventoryPage.cartBadgeCount).toHaveText(String(EXPECTED_PRODUCT_COUNT));

    // Open cart
    await inventoryPage.openCart();

    // Verify all 6 items in cart
    await cartPage.waitForPageLoad();
    await expect(cartPage.cartItems).toHaveCount(EXPECTED_PRODUCT_COUNT);
  });

  test("should verify product count and prices", async ({ authenticatedPage }) => {
    const { inventoryPage } = authenticatedPage;

    // Verify 6 products loaded
    await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);

    // Verify all prices are valid numbers > 0
    const prices = await inventoryPage.getAllProductPrices();
    expect(prices).toHaveLength(EXPECTED_PRODUCT_COUNT);
    for (const price of prices) {
      expect(price).toBeGreaterThan(0);
      expect(price).toBeLessThanOrEqual(100);
    }
  });

  test("should add all 6 products and verify product names", async ({
    authenticatedPage,
  }) => {
    const { inventoryPage, cartPage } = authenticatedPage;

    // Add all 6 products
    for (let i = 0; i < 6; i++) {
      await inventoryPage.addProductToCart(i);
    }

    // Open cart
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();

    // Wait for the products to render before reading the list
    await expect(cartPage.cartItems).toHaveCount(EXPECTED_PRODUCT_COUNT);

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
