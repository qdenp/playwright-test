/**
 * Browser navigation tests
 */
import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";
import { CheckoutPage } from "../page-objects/CheckoutPage.js";

test.describe("Browser Navigation", () => {
  test("should handle browser back button from cart to inventory", async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);
    const cartPage = new CartPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    await inventoryPage.addProductToCart(0);
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();

    // Go back to inventory
    await page.goBack();
    await inventoryPage.waitForPageLoad();

    await expect(page).toHaveURL(/.*inventory/);
    await expect(inventoryPage.headerTitle).toContainText("Products");
  });

  test("should handle browser forward button after back", async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);
    const cartPage = new CartPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    await inventoryPage.addProductToCart(0);
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();

    // Go back
    await page.goBack();
    await inventoryPage.waitForPageLoad();

    // Go forward
    await page.goForward();
    await cartPage.waitForPageLoad();

    await expect(page).toHaveURL(/.*cart/);
  });

  test("should handle back from checkout to cart", async ({ page }) => {
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

    // Go back to cart
    await page.goBack();
    await cartPage.waitForPageLoad();

    await expect(page).toHaveURL(/.*cart/);
  });

  test("should preserve cart state after multiple back/forward", async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);
    const cartPage = new CartPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    await inventoryPage.addProductToCart(0);
    await inventoryPage.addProductToCart(1);
    await expect(inventoryPage.cartBadgeCount).toHaveText("2");

    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();

    // Navigate back and forth
    await page.goBack();
    await inventoryPage.waitForPageLoad();
    await page.goForward();
    await cartPage.waitForPageLoad();

    // Cart should still have 2 items
    await expect(inventoryPage.cartBadgeCount).toHaveText("2");
  });
});
