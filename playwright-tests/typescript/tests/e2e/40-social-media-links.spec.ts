import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Test 40: Social Media Links
 * Проверка footer и структуры страницы
 */
test.describe("Social Media Links", () => {
  test("01 - should verify page loads correctly after login", async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    // Login
    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    // Verify inventory page loaded correctly
    await expect(inventoryPage.products.first()).toBeVisible();
    expect(await inventoryPage.getProductCount()).toBe(6);
  });

  test("02 - should verify page structure and elements", async ({ page }) => {
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
});
