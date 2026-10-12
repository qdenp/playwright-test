/**
 * Test 31: Responsive design tests
 */
import { test, expect } from '../fixtures.js';
import { CheckoutPage } from '../page-objects/CheckoutPage.js';
import { EXPECTED_PRODUCT_COUNT } from '../config';

test.describe('Responsive Design', () => {
  test('01 - should work on mobile viewport (375x667)', async ({ page, authenticatedPage }) => {
    await page.setViewportSize({ width: 375, height: 667 });

    const { loginPage, inventoryPage } = authenticatedPage;

    await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);

    await loginPage.openHamburgerMenu();
    await expect(loginPage.menuOverlay).toBeVisible();
    await loginPage.closeHamburgerMenu();
  });

  test('02 - should work on tablet viewport (768x1024)', async ({ page, authenticatedPage }) => {
    await page.setViewportSize({ width: 768, height: 1024 });

    const { inventoryPage } = authenticatedPage;

    await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);
  });

  test('03 - should handle viewport resize during interaction', async ({
    page,
    authenticatedPage,
  }) => {
    const { inventoryPage } = authenticatedPage;

    // Resize to mobile
    await page.setViewportSize({ width: 375, height: 667 });
    await inventoryPage.addProductToCart(0);

    // Resize to tablet
    await page.setViewportSize({ width: 768, height: 1024 });
    await expect(
      inventoryPage.cartBadge.locator('.shopping_cart_badge'),
    ).toHaveText('1');
  });

  test('04 - should add to cart on mobile viewport', async ({ page, authenticatedPage }) => {
    await page.setViewportSize({ width: 375, height: 667 });

    const { inventoryPage } = authenticatedPage;

    await inventoryPage.addProductToCart(0);
    await inventoryPage.addProductToCart(1);

    await expect(
      inventoryPage.cartBadge.locator('.shopping_cart_badge'),
    ).toHaveText('2');
  });

  test('05 - should checkout on tablet viewport', async ({ page, authenticatedPage }) => {
    await page.setViewportSize({ width: 768, height: 1024 });

    const { inventoryPage, cartPage, checkoutPage } = authenticatedPage;

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
