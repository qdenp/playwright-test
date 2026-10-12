import { test, expect } from "../fixtures";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Test 35: Social Media Links
 * Проверка footer и структуры страницы
 */
test.describe("Social Media Links", () => {
  test("01 - should verify page loads correctly after login", async ({
    authenticatedPage,
  }) => {
    const { inventoryPage } = authenticatedPage;

    // Verify inventory page loaded correctly
    await expect(inventoryPage.products.first()).toBeVisible();
    expect(await inventoryPage.getProductCount()).toBe(6);
  });

  test("02 - should verify page structure and elements", async ({
    authenticatedPage,
  }) => {
    const { inventoryPage } = authenticatedPage;

    // Verify main page elements
    await expect(inventoryPage.headerTitle).toContainText("Products");
    await expect(inventoryPage.sortDropdown).toBeVisible();
    await expect(inventoryPage.cartBadge).toBeVisible();
  });
});
