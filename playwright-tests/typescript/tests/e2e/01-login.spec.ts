import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { EXPECTED_PRODUCT_COUNT } from "../config";

/**
 * Успешный логин — стандартный пользователь
 */
test("Успешный логин — стандартный пользователь", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Verify we're on the products page
  await expect(inventoryPage.headerTitle).toHaveText("Products");
  await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);
});
