/**
 * Verify product images are displayed
 */
import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { EXPECTED_PRODUCT_COUNT } from "../config.js";

test("should display all product images", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Verify all 6 product images are visible
  await expect(inventoryPage.productImages).toHaveCount(EXPECTED_PRODUCT_COUNT);
  await expect(inventoryPage.productImages.first()).toBeVisible();

  // Verify each image has non-empty src attribute
  for (let i = 0; i < EXPECTED_PRODUCT_COUNT; i++) {
    await expect(inventoryPage.productImages.nth(i)).toHaveAttribute("src", /^.{1,}$/);
  }
});
