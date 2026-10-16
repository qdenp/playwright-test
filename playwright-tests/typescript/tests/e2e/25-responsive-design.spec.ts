/**
 * Responsive design tests
 */
import { test, expect } from "../fixtures";
import { CheckoutPage } from "../page-objects/CheckoutPage";
import { EXPECTED_PRODUCT_COUNT, timeouts } from "../config";

test.describe('Responsive Design', () => {
  test('should work on mobile viewport (375x667)', async ({ page, authenticatedPage }) => {
    await page.setViewportSize({ width: 375, height: 667 });

    const { inventoryPage } = authenticatedPage;

    await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);

    await inventoryPage.openHamburgerMenu();
    await expect(inventoryPage.menuOverlay).toBeVisible();
    await inventoryPage.closeHamburgerMenu();
  });

  test('should work on tablet viewport (768x1024)', async ({ page, authenticatedPage }) => {
    await page.setViewportSize({ width: 768, height: 1024 });

    const { inventoryPage } = authenticatedPage;

    await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);
  });

  test('should handle viewport resize during interaction', async ({
    page,
    authenticatedPage,
  }) => {
    const { inventoryPage } = authenticatedPage;

    // Resize to mobile
    await page.setViewportSize({ width: 375, height: 667 });
    await inventoryPage.addProductToCart(0);

    // Resize to tablet
    await page.setViewportSize({ width: 768, height: 1024 });
    await expect(inventoryPage.cartBadgeCount).toHaveText('1');
  });

  test('should add to cart on mobile viewport', async ({ page, authenticatedPage }) => {
    await page.setViewportSize({ width: 375, height: 667 });

    const { inventoryPage } = authenticatedPage;

    await inventoryPage.addProductToCart(0);
    await inventoryPage.addProductToCart(1);

    await expect(inventoryPage.cartBadgeCount).toHaveText('2');
  });

  test('should checkout on tablet viewport', async ({ page, authenticatedPage }) => {
    await page.setViewportSize({ width: 768, height: 1024 });

    const { inventoryPage, cartPage, checkoutPage } = authenticatedPage;

    await inventoryPage.addProductToCart(0);
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();

    await cartPage.checkout();
    await checkoutPage.waitForPageLoad();

    await checkoutPage.fillInfo('Test', 'User', '12345');
    await checkoutPage.clickContinue();

    await expect(checkoutPage.finishButton).toBeVisible({ timeout: timeouts.DEFAULT });
  });
});
