import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Test 33: Navigation - verify page URLs
 */
test("33 - should navigate to products page", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  await expect(page).toHaveURL(/.*inventory/);
  await expect(inventoryPage.headerTitle).toContainText("Products");

  await inventoryPage.addProductToCart(0);
  await expect(
    inventoryPage.cartBadge.locator(".shopping_cart_badge"),
  ).toHaveText("1");
});
