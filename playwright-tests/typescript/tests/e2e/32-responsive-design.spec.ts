/**
 * Test 32: Responsive design tests
 */
import { test, expect } from '../fixtures.js';
import { LoginPage } from '../page-objects/LoginPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';
import { CartPage } from '../page-objects/CartPage.js';
import { CheckoutPage } from '../page-objects/CheckoutPage.js';

test.describe('Responsive Design', () => {
  test('01 - should work on mobile viewport (375x667)', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });

    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    const productCount = await inventoryPage.getProductCount();
    expect(productCount).toBe(6);

    await loginPage.openHamburgerMenu();
    await expect(loginPage.menuOverlay).toBeVisible();
    await loginPage.closeHamburgerMenu();
  });

  test('02 - should work on tablet viewport (768x1024)', async ({ page }) => {
    await page.setViewportSize({ width: 768, height: 1024 });

    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    const productCount = await inventoryPage.getProductCount();
    expect(productCount).toBe(6);
  });

  test('03 - should handle viewport resize during interaction', async ({
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
    await expect(
      inventoryPage.cartBadge.locator('.shopping_cart_badge'),
    ).toHaveText('1');
  });

  test('04 - should add to cart on mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });

    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    await inventoryPage.addProductToCart(0);
    await inventoryPage.addProductToCart(1);

    await expect(
      inventoryPage.cartBadge.locator('.shopping_cart_badge'),
    ).toHaveText('2');
  });

  test('05 - should checkout on tablet viewport', async ({ page }) => {
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

    await checkoutPage.fillInfo('Test', 'User', '12345');
    await checkoutPage.clickContinue();

    await expect(checkoutPage.finishButton).toBeVisible({ timeout: 10000 });
  });
});
