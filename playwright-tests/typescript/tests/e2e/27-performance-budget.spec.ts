/**
 * Performance budget tests
 */
import { test, expect } from "../fixtures";
import { EXPECTED_PRODUCT_COUNT, config, performanceBudgets } from "../config";

test.describe('Performance Budget', () => {
  test('should load login page within 2 seconds', async ({ page }) => {
    const startTime = Date.now();
    await page.goto(config.baseUrl);
    const loadTime = Date.now() - startTime;

    // Allow up to 3 seconds for slower browsers (Firefox, WebKit)
    expect(loadTime).toBeLessThan(performanceBudgets.LOGIN_PAGE);
    console.log(`Login page loaded in ${loadTime}ms`);
  });

  test('should load inventory page within 3 seconds after login', async ({
    authenticatedPage,
  }) => {
    const { inventoryPage } = authenticatedPage;

    const startTime = Date.now();
    await inventoryPage.waitForPageLoad();
    const loadTime = Date.now() - startTime;

    expect(loadTime).toBeLessThan(performanceBudgets.INVENTORY_PAGE);
    console.log(`Inventory page loaded in ${loadTime}ms`);
  });

  test('should load all 6 products within performance budget', async ({
    authenticatedPage,
  }) => {
    const { inventoryPage } = authenticatedPage;

    const startTime = Date.now();
    await inventoryPage.waitForPageLoad();
    const loadTime = Date.now() - startTime;

    expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);
    expect(loadTime).toBeLessThan(performanceBudgets.PRODUCTS_LIST);
    console.log(`All 6 products loaded in ${loadTime}ms`);
  });

  test('should load cart page within 2 seconds', async ({ authenticatedPage }) => {
    const { inventoryPage, cartPage } = authenticatedPage;

    const startTime = Date.now();
    await inventoryPage.addProductToCart(0);
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();
    const loadTime = Date.now() - startTime;

    expect(loadTime).toBeLessThan(performanceBudgets.CART_PAGE);
    console.log(`Cart page loaded in ${loadTime}ms`);
  });

  test('should verify page load consistency (3 runs)', async ({
    authenticatedPage,
  }) => {
    const { inventoryPage } = authenticatedPage;

    const loadTimes: number[] = [];
    for (let i = 0; i < 3; i++) {
      const startTime = Date.now();
      await inventoryPage.waitForPageLoad();
      loadTimes.push(Date.now() - startTime);
    }

    const avgLoadTime = loadTimes.reduce((a, b) => a + b, 0) / loadTimes.length;
    console.log(`Average load time: ${avgLoadTime}ms`);
    expect(avgLoadTime).toBeLessThan(performanceBudgets.AVERAGE_LOAD);
  });

  test('should verify product images load within budget', async ({
    authenticatedPage,
  }) => {
    const { page, inventoryPage } = authenticatedPage;

    await inventoryPage.waitForPageLoad();

    const startTime = Date.now();
    const images = page.locator('.inventory_item_img');
    await images.first().waitFor({ state: 'visible' });
    const loadTime = Date.now() - startTime;

    expect(loadTime).toBeLessThan(performanceBudgets.IMAGES);
    console.log(`Images loaded in ${loadTime}ms`);
  });
});
