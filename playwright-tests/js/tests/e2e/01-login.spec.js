import { test, expect } from "../fixtures.js";

/**
 * Successful login with the standard user
 */
test("Successful login with the standard user", async ({
  authenticatedPage,
}) => {
  const { page, inventoryPage } = authenticatedPage;

  await expect(page).toHaveURL(/.*inventory\.html/);
  await expect(inventoryPage.headerTitle).toHaveText("Products");
  await expect(inventoryPage.products.first()).toBeVisible();
});
