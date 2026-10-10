import { test, expect } from '../fixtures';
import { LoginPage } from '../page-objects/LoginPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';
import { CartPage } from '../page-objects/CartPage.js';
import { CheckoutPage } from '../page-objects/CheckoutPage.js';

/**
 * Test 06: Полный flow покупки (E2E)
 */
test('06 - should complete full purchase flow', async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);
  const cartPage = new CartPage(page);
  const checkoutPage = new CheckoutPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Add 2 products to cart
  await inventoryPage.addProductToCart(0);
  await inventoryPage.addProductToCart(1);
  await expect(inventoryPage.cartBadge.locator('.shopping_cart_badge')).toHaveText('2');

  // Open cart
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  // Checkout
  await cartPage.checkout();
  await checkoutPage.waitForPageLoad();

  // Fill info
  await checkoutPage.fillInfo('Иван', 'Иванов', '100000');
  await checkoutPage.clickContinue();

  // Complete order
  await expect(checkoutPage.finishButton).toBeVisible({ timeout: 10000 });
  await checkoutPage.clickFinish();

  // Verify success
  await expect(checkoutPage.completeTitle).toBeVisible({ timeout: 10000 });
  await expect(checkoutPage.completeTitle).toContainText('Thank you for your order');
});
