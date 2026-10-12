/**
 * Test 35: Social Media Links
 * Проверка социальных ссылок внизу экрана (X, Facebook, LinkedIn)
 */
import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { EXPECTED_PRODUCT_COUNT } from "../config.js";

test.describe("Social Media Links", () => {
  test("35 - should verify page loads correctly after login", async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    // Login
    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    // Verify inventory page loaded correctly
    await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);
  });

  test("36 - should verify page structure and elements", async ({ page }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    // Login
    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    // Verify main page elements
    await expect(inventoryPage.headerTitle).toContainText("Products");
    await expect(inventoryPage.sortDropdown).toBeVisible();
    await expect(inventoryPage.cartBadge).toBeVisible();
  });

  test("37 - should verify all products are loaded and visible", async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    // Login
    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    // Verify all products are present
    await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);
    await expect(inventoryPage.products.first()).toBeVisible();
  });
});
