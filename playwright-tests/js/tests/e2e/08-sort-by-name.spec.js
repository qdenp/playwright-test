import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage";
import { InventoryPage } from "../page-objects/InventoryPage";
import { EXPECTED_PRODUCT_COUNT } from "../config";

/**
 * Sorting products by name (a to z)
 */
test("Sorting products by name (a to z)", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  await inventoryPage.sortByAlphaAtoZ();

  // The list has fully rendered - it holds 6 products
  await expect(inventoryPage.productNames).toHaveCount(EXPECTED_PRODUCT_COUNT);

  const names = await inventoryPage.getAllProductNames();

  for (let i = 1; i < names.length; i++) {
    expect(names[i].toLowerCase() > names[i - 1].toLowerCase()).toBeTruthy();
  }
});
