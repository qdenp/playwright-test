/**
 * Product cards on the inventory page
 */
import { test, expect } from "../fixtures.js";
import { EXPECTED_PRODUCT_COUNT } from "../config.js";

test("Product cards on the inventory page", async ({ authenticatedPage }) => {
  const { inventoryPage } = authenticatedPage;

  // Each card carries a name, description, price and an add-to-cart button
  await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);
  await expect(inventoryPage.productNames).toHaveCount(EXPECTED_PRODUCT_COUNT);
  await expect(inventoryPage.productDescriptions).toHaveCount(EXPECTED_PRODUCT_COUNT);
  await expect(inventoryPage.productPrices).toHaveCount(EXPECTED_PRODUCT_COUNT);
  await expect(inventoryPage.addToCartButtons).toHaveCount(EXPECTED_PRODUCT_COUNT);

  // All elements are non-empty and visible
  await expect(inventoryPage.productNames.first()).not.toBeEmpty();
  await expect(inventoryPage.productDescriptions.first()).not.toBeEmpty();
  await expect(inventoryPage.productPrices.first()).toContainText("$");
  await expect(inventoryPage.addToCartButtons.first()).toBeVisible();
});
