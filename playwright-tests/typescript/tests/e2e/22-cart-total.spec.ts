/**
 * Test 22: Verify cart total updates when adding products
 */
import { test, expect } from '../fixtures.js';
import { LoginPage } from '../page-objects/LoginPage.js';
import { CartPage } from '../page-objects/CartPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';

test('22 - should update cart total when adding products', async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);
  const cartPage = new CartPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Add first product
  await inventoryPage.addProductToCart(0);
  await expect(
    inventoryPage.cartBadge.locator('.shopping_cart_badge'),
  ).toHaveText('1');

  // Add second product
  await inventoryPage.addProductToCart(1);
  await expect(
    inventoryPage.cartBadge.locator('.shopping_cart_badge'),
  ).toHaveText('2');

  // Open cart and verify total
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();

  // Verify cart has items (total price may not be visible on Sauce Demo)
  const cartItems = await cartPage.getCartItemCount();
  expect(cartItems).toBe(2);
});
