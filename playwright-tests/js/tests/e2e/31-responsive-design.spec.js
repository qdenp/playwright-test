/**
 * Test 31: Responsive design tests
 */
import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";
import { CheckoutPage } from "../page-objects/CheckoutPage.js";
import { timeouts, EXPECTED_PRODUCT_COUNT } from "../config.js";

test.describe("Responsive Design", () => {
  test("should work on mobile viewport (375x667)", async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });

    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);

    await loginPage.openHamburgerMenu();
    await expect(loginPage.menuOverlay).toBeVisible();
    await loginPage.closeHamburgerMenu();
  });

  test("should work on tablet viewport (768x1024)", async ({ page }) => {
    await page.setViewportSize({ width: 768, height: 1024 });

    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);
  });

  test("should handle viewport resize during interaction", async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    // Resize to mobile
    await page.setViewportSize({ width: 375, height: 667 });
    await inventoryPage.addProductToCart(0);

    // Resize to tablet
    await page.setViewportSize({ width: 768, height: 1024 });
    await expect(inventoryPage.cartBadgeCount).toHaveText("1");
  });

  test("should add to cart on mobile viewport", async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });

    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    await inventoryPage.addProductToCart(0);
    await inventoryPage.addProductToCart(1);

    await expect(inventoryPage.cartBadgeCount).toHaveText("2");
  });

  test("should checkout on tablet viewport", async ({ page }) => {
    await page.setViewportSize({ width: 768, height: 1024 });

    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);
    const cartPage = new CartPage(page);
    const checkoutPage = new CheckoutPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    await inventoryPage.addProductToCart(0);
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();

    await cartPage.checkout();
    await checkoutPage.waitForPageLoad();

    await checkoutPage.fillInfo("Test", "User", "12345");
    await checkoutPage.clickContinue();

    await expect(checkoutPage.finishButton).toBeVisible({ timeout: timeouts.DEFAULT });
  });
});
