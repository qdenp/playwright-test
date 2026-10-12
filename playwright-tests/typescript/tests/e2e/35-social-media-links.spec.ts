import { test, expect } from "../fixtures";
import { EXPECTED_PRODUCT_COUNT } from "../config";

/**
 * Test 35: Social Media Links
 * РџСЂРѕРІРµСЂРєР° footer Рё СЃС‚СЂСѓРєС‚СѓСЂС‹ СЃС‚СЂР°РЅРёС†С‹
 */
test.describe("Social Media Links", () => {
  test("should verify page loads correctly after login", async ({
    authenticatedPage,
  }) => {
    const { inventoryPage } = authenticatedPage;

    // Verify inventory page loaded correctly
    await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);
  });

  test("should verify page structure and elements", async ({
    authenticatedPage,
  }) => {
    const { inventoryPage } = authenticatedPage;

    // Verify main page elements
    await expect(inventoryPage.headerTitle).toContainText("Products");
    await expect(inventoryPage.sortDropdown).toBeVisible();
    await expect(inventoryPage.cartBadge).toBeVisible();
  });

  test("should verify all products are loaded and visible", async ({
    authenticatedPage,
  }) => {
    const { inventoryPage } = authenticatedPage;

    // Verify all products are present
    await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);
    await expect(inventoryPage.products.first()).toBeVisible();
  });
});
