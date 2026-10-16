import { test, expect } from "../fixtures";
import { EXPECTED_PRODUCT_COUNT } from "../config";

test("should display all product images", async ({ authenticatedPage }) => {
  const { inventoryPage } = authenticatedPage;

  // Verify there are exactly 6 product images and every one of them is visible
  await expect(inventoryPage.productImages).toHaveCount(EXPECTED_PRODUCT_COUNT);

  for (let i = 0; i < EXPECTED_PRODUCT_COUNT; i++) {
    await expect(inventoryPage.productImages.nth(i)).toBeVisible();
  }

  // Verify each image has a non-empty src attribute
  for (let i = 0; i < EXPECTED_PRODUCT_COUNT; i++) {
    await expect(inventoryPage.productImages.nth(i)).toHaveAttribute("src", /^.{1,}$/);
  }
});
