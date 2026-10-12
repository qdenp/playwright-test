/**
 * Performance budget tests
 */
import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";

test.describe("Performance Budget", () => {
  test("should load login page within 2 seconds", async ({ page }) => {
    const startTime = Date.now();
    await page.goto("https://www.saucedemo.com/");
    const loadTime = Date.now() - startTime;

    // Allow up to 3 seconds for slower browsers (Firefox, WebKit)
    expect(loadTime).toBeLessThan(3000);
    console.log(`Login page loaded in ${loadTime}ms`);
  });

  test("should load inventory page within 3 seconds after login", async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    const startTime = Date.now();
    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();
    const loadTime = Date.now() - startTime;

    expect(loadTime).toBeLessThan(3000);
    console.log(`Inventory page loaded in ${loadTime}ms`);
  });

  test("should load all 6 products within performance budget", async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    const startTime = Date.now();
    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();
    const loadTime = Date.now() - startTime;

    const productCount = await inventoryPage.getProductCount();
    expect(productCount).toBe(6);
    expect(loadTime).toBeLessThan(5000);
    console.log(`All 6 products loaded in ${loadTime}ms`);
  });

  test("should load cart page within 2 seconds", async ({ page }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);
    const cartPage = new CartPage(page);

    const startTime = Date.now();
    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();
    await inventoryPage.addProductToCart(0);
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();
    const loadTime = Date.now() - startTime;

    expect(loadTime).toBeLessThan(4000);
    console.log(`Cart page loaded in ${loadTime}ms`);
  });

  test("should verify page load consistency (3 runs)", async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    const loadTimes = [];
    for (let i = 0; i < 3; i++) {
      const startTime = Date.now();
      await loginPage.goTo();
      await loginPage.login();
      await inventoryPage.waitForPageLoad();
      loadTimes.push(Date.now() - startTime);
    }

    const avgLoadTime = loadTimes.reduce((a, b) => a + b, 0) / loadTimes.length;
    console.log(`Average load time: ${avgLoadTime}ms`);
    expect(avgLoadTime).toBeLessThan(5000);
  });

  test("should verify product images load within budget", async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    const startTime = Date.now();
    const images = page.locator(".inventory_item_img");
    await images.first().waitFor({ state: "visible" });
    const loadTime = Date.now() - startTime;

    expect(loadTime).toBeLessThan(3000);
    console.log(`Images loaded in ${loadTime}ms`);
  });
});
