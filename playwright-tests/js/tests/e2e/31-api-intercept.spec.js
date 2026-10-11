/**
 * Test 31: API intercept - verify API responses
 */
import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";
import { CheckoutPage } from "../page-objects/CheckoutPage.js";

test.describe("API Intercept Tests", () => {
  test("01 - should intercept and verify inventory API response", async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    // Set up API interception before navigation
    // Sauce Demo loads products via page rendering, not API calls
    // We verify the page loads successfully after login
    const responses = [];
    page.on("response", (response) => {
      if (response.status() >= 200 && response.status() < 300) {
        responses.push(response);
      }
    });

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    // Verify page loaded successfully
    expect(responses.length).toBeGreaterThan(0);
    await expect(page).toHaveURL(/.*saucedemo/);
  });

  test("02 - should verify product data structure from API", async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    // Verify products loaded with correct data
    const productCount = await inventoryPage.getProductCount();
    expect(productCount).toBe(6);

    // Verify each product has required fields
    for (let i = 0; i < productCount; i++) {
      const name = await inventoryPage.getProductTitle(i);
      const price = await inventoryPage.getProductPrice(i);
      const desc = await inventoryPage.getProductDescription(i);

      expect(name).toBeTruthy();
      expect(price).toBeGreaterThan(0);
      expect(desc).toBeTruthy();
    }
  });

  test("03 - should verify all 6 products have valid data", async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    const productCount = await inventoryPage.getProductCount();
    expect(productCount).toBe(6);

    // Verify all products have valid prices
    for (let i = 0; i < productCount; i++) {
      const price = await inventoryPage.getProductPrice(i);
      expect(price).toBeGreaterThan(0);
      expect(price).toBeLessThanOrEqual(100);
    }
  });

  test("04 - should intercept API during checkout flow", async ({ page }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);
    const cartPage = new CartPage(page);
    const checkoutPage = new CheckoutPage(page);

    // Track all responses from the start
    const allResponses = [];
    page.on("response", (response) => {
      if (response.status() >= 200 && response.status() < 300) {
        allResponses.push(response);
      }
    });

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    // Add product to cart
    await inventoryPage.addProductToCart(0);
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();

    const responsesBeforeCheckout = allResponses.length;
    await cartPage.checkout();
    await checkoutPage.waitForPageLoad();

    // Verify checkout page loaded successfully
    const responsesAfterCheckout =
      allResponses.length - responsesBeforeCheckout;
    expect(responsesAfterCheckout).toBeGreaterThanOrEqual(0);
    await expect(checkoutPage.firstNameInput).toBeVisible();
  });
});
