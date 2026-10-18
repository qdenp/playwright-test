/**
 * Performance budget tests
 * Measurements use warmup + averaging to avoid cold-start noise (AGENTS.md §3.2).
 *
 * Inventory-page measurements use re-login (not page.reload()) because the
 * test site is a static SPA on GitHub Pages: /inventory.html triggers a 404
 * fallback that redirects to / (login). The auth cookie survives, but
 * page.reload() lands on the login page instead of the inventory page.
 */
import { test, expect } from "../fixtures";
import { EXPECTED_PRODUCT_COUNT, config, performanceBudgets } from "../config";

const MEASUREMENT_COUNT = 3;

test.describe("Performance Budget", () => {
  test("should load login page within 2 seconds", async ({ page }) => {
    // Warmup — prime caches
    await page.goto(config.baseUrl);
    await page.waitForLoadState("networkidle");

    const loadTimes: number[] = [];
    for (let i = 0; i < MEASUREMENT_COUNT; i++) {
      const startTime = Date.now();
      await page.goto(config.baseUrl);
      await page.waitForLoadState("networkidle");
      loadTimes.push(Date.now() - startTime);
    }

    const avgLoadTime = loadTimes.reduce((a, b) => a + b, 0) / loadTimes.length;
    expect(avgLoadTime).toBeLessThan(performanceBudgets.LOGIN_PAGE);
    console.log(`Login page avg load time: ${avgLoadTime}ms over ${MEASUREMENT_COUNT} runs`);
  });

  test("should load inventory page within 3 seconds after login", async ({
    authenticatedPage,
  }) => {
    const { loginPage, inventoryPage } = authenticatedPage;

    // Warmup — re-login once to prime caches after the fixture's initial login
    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    const loadTimes: number[] = [];
    for (let i = 0; i < MEASUREMENT_COUNT; i++) {
      const startTime = Date.now();
      await loginPage.goTo();
      await loginPage.login();
      await inventoryPage.waitForPageLoad();
      loadTimes.push(Date.now() - startTime);
    }

    const avgLoadTime = loadTimes.reduce((a, b) => a + b, 0) / loadTimes.length;
    expect(avgLoadTime).toBeLessThan(performanceBudgets.INVENTORY_PAGE);
    console.log(`Inventory page avg load time: ${avgLoadTime}ms over ${MEASUREMENT_COUNT} runs`);
  });

  test("should load all 6 products within performance budget", async ({
    authenticatedPage,
  }) => {
    const { loginPage, inventoryPage } = authenticatedPage;

    // Warmup
    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    const loadTimes: number[] = [];
    for (let i = 0; i < MEASUREMENT_COUNT; i++) {
      const startTime = Date.now();
      await loginPage.goTo();
      await loginPage.login();
      await inventoryPage.waitForPageLoad();
      loadTimes.push(Date.now() - startTime);
    }

    const avgLoadTime = loadTimes.reduce((a, b) => a + b, 0) / loadTimes.length;
    expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);
    expect(avgLoadTime).toBeLessThan(performanceBudgets.PRODUCTS_LIST);
    console.log(`All 6 products avg load time: ${avgLoadTime}ms over ${MEASUREMENT_COUNT} runs`);
  });

  test("should load cart page within 2 seconds", async ({ authenticatedPage }) => {
    const { inventoryPage, cartPage } = authenticatedPage;

    // Warmup — add product, open cart, then return to inventory
    await inventoryPage.addProductToCart(0);
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();
    await cartPage.continueShopping();
    await inventoryPage.waitForPageLoad();

    const loadTimes: number[] = [];
    for (let i = 0; i < MEASUREMENT_COUNT; i++) {
      const startTime = Date.now();
      await inventoryPage.openCart();
      await cartPage.waitForPageLoad();
      loadTimes.push(Date.now() - startTime);
      // Navigate back to inventory for the next iteration
      await cartPage.continueShopping();
      await inventoryPage.waitForPageLoad();
    }

    const avgLoadTime = loadTimes.reduce((a, b) => a + b, 0) / loadTimes.length;
    expect(avgLoadTime).toBeLessThan(performanceBudgets.CART_PAGE);
    console.log(`Cart page avg load time: ${avgLoadTime}ms over ${MEASUREMENT_COUNT} runs`);
  });

  test("should verify page load consistency (3 runs with warmup)", async ({
    authenticatedPage,
  }) => {
    const { loginPage, inventoryPage } = authenticatedPage;

    // Warmup
    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    const loadTimes: number[] = [];
    for (let i = 0; i < MEASUREMENT_COUNT; i++) {
      const startTime = Date.now();
      await loginPage.goTo();
      await loginPage.login();
      await inventoryPage.waitForPageLoad();
      loadTimes.push(Date.now() - startTime);
    }

    const avgLoadTime = loadTimes.reduce((a, b) => a + b, 0) / loadTimes.length;
    console.log(`Average load time: ${avgLoadTime}ms`);
    expect(avgLoadTime).toBeLessThan(performanceBudgets.AVERAGE_LOAD);
  });

  test("should verify product images load within budget", async ({
    authenticatedPage,
  }) => {
    const { loginPage, inventoryPage } = authenticatedPage;

    // Warmup
    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();
    await inventoryPage.productImages.first().waitFor({ state: "visible" });

    const loadTimes: number[] = [];
    for (let i = 0; i < MEASUREMENT_COUNT; i++) {
      const startTime = Date.now();
      await loginPage.goTo();
      await loginPage.login();
      await inventoryPage.waitForPageLoad();
      await inventoryPage.productImages.first().waitFor({ state: "visible" });
      loadTimes.push(Date.now() - startTime);
    }

    const avgLoadTime = loadTimes.reduce((a, b) => a + b, 0) / loadTimes.length;
    expect(avgLoadTime).toBeLessThan(performanceBudgets.IMAGES);
    console.log(`Images avg load time: ${avgLoadTime}ms over ${MEASUREMENT_COUNT} runs`);
  });
});
