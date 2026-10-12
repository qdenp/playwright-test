import { test, expect } from "../fixtures.js";
import { EXPECTED_PRODUCT_COUNT } from "../config.js";

/**
 * Sorting products by name (a to z)
 */
test("Sorting products by name (a to z)", async ({ authenticatedPage }) => {
  const { inventoryPage } = authenticatedPage;

  await inventoryPage.sortByAlphaAtoZ();

  // The list has fully rendered - it holds 6 products
  await expect(inventoryPage.productNames).toHaveCount(EXPECTED_PRODUCT_COUNT);

  const names = await inventoryPage.getAllProductNames();

  for (let i = 1; i < names.length; i++) {
    expect(names[i].toLowerCase() > names[i - 1].toLowerCase()).toBeTruthy();
  }
});
