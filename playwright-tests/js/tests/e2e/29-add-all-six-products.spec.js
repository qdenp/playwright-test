/**
 * Add all 6 products and verify
 */
import { test, expect } from "../fixtures";
import { CartPage } from "../page-objects/CartPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { EXPECTED_PRODUCT_COUNT } from "../config.js";

async function login(page) {
  const loginPage = new LoginPage(page);
  await loginPage.goTo();
  await loginPage.login();
  const inventoryPage = new InventoryPage(page);
  await inventoryPage.waitForPageLoad();
  return inventoryPage;
}

test.describe("Add All 6 Products", () => {
  test("should add all 6 products and verify cart", async ({ page }) => {
    const inventoryPage = await login(page);
    const cartPage = new CartPage(page);

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

  test("should verify product count and prices", async ({ page }) => {
    const inventoryPage = await login(page);

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

  test("should add all 6 products and verify product names", async ({ page }) => {
    const inventoryPage = await login(page);
    const cartPage = new CartPage(page);

    // Add all 6 products
    for (let i = 0; i < EXPECTED_PRODUCT_COUNT; i++) {
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

    // Р–РґС‘Рј РѕС‚СЂРёСЃРѕРІРєРё С‚РѕРІР°СЂРѕРІ, РїСЂРµР¶РґРµ С‡РµРј С‡РёС‚Р°С‚СЊ СЃРїРёСЃРѕРє
    await expect(cartPage.cartItems).toHaveCount(EXPECTED_PRODUCT_COUNT);

    const cartItems = await cartPage.getCartItemNames();
    expect(cartItems).toHaveLength(EXPECTED_PRODUCT_COUNT);

    for (const name of expectedNames) {
      expect(cartItems).toContain(name);
    }
  });
});
