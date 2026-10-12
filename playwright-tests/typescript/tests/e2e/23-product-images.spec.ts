import { test, expect } from "../fixtures.js";
import { EXPECTED_PRODUCT_COUNT } from "../config.js";

/**
 * Test 23: Verify product images are displayed
 */
test("23 - should display all product images", async ({ authenticatedPage }) => {
  const { inventoryPage } = authenticatedPage;

  // Verify all 6 product images are visible
  await expect(inventoryPage.productImages).toHaveCount(EXPECTED_PRODUCT_COUNT);
  await expect(inventoryPage.productImages.first()).toBeVisible();

  // Verify each image has non-empty src attribute
  for (let i = 0; i < EXPECTED_PRODUCT_COUNT; i++) {
    await expect(inventoryPage.productImages.nth(i)).toHaveAttribute("src", /^.{1,}$/);
  }
});
