/**
 * Test 29: Specific item combinations (3 of 6 products)
 */
import { test, expect } from '../fixtures';
import { CartPage } from '../page-objects/CartPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';

test.describe('Item Combinations', () => {
  test('01 - should add cheapest 3 products and verify cart', async ({ page }) => {
    // Login
    await page.goto('/');
    await page.locator('#user-name').fill('standard_user');
    await page.locator('#password').fill('secret_sauce');
    await page.locator('#login-button').click();
    
    const inventoryPage = new InventoryPage(page);
    await inventoryPage.waitForPageLoad();
    
    const cartPage = new CartPage(page);

    // Add 3 cheapest: Onesie (7.99), Bike Light (9.99), Red T-Shirt (15.99)
    await inventoryPage.addProductToCart(4); // Onesie - $7.99
    await inventoryPage.addProductToCart(1); // Bike Light - $9.99
    await inventoryPage.addProductToCart(5); // Red T-Shirt - $15.99

    // Verify badge
    await expect(inventoryPage.cartBadge.locator('.shopping_cart_badge')).toHaveText('3');

    // Open cart and verify items
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();

    const cartItems = await cartPage.getCartItemNames();
    expect(cartItems).toContain('Sauce Labs Onesie');
    expect(cartItems).toContain('Sauce Labs Bike Light');
    expect(cartItems).toContain('Test.allTheThings() T-Shirt (Red)');
  });

  test('02 - should add most expensive 3 products and verify cart', async ({ page }) => {
    // Login
    await page.goto('/');
    await page.locator('#user-name').fill('standard_user');
    await page.locator('#password').fill('secret_sauce');
    await page.locator('#login-button').click();
    
    const inventoryPage = new InventoryPage(page);
    await inventoryPage.waitForPageLoad();
    
    const cartPage = new CartPage(page);

    // Add 3 most expensive: Fleece Jacket (49.99), Backpack (29.99), Bolt T-Shirt (24.99)
    await inventoryPage.addProductToCart(3); // Fleece Jacket - $49.99
    await inventoryPage.addProductToCart(0); // Backpack - $29.99
    await inventoryPage.addProductToCart(2); // Bolt T-Shirt - $24.99

    // Verify badge
    await expect(inventoryPage.cartBadge.locator('.shopping_cart_badge')).toHaveText('3');

    // Open cart and verify items
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();

    const cartItems = await cartPage.getCartItemNames();
    expect(cartItems).toContain('Sauce Labs Fleece Jacket');
    expect(cartItems).toContain('Sauce Labs Backpack');
    expect(cartItems).toContain('Sauce Labs Bolt T-Shirt');
  });

  test('03 - should add alternating products (1st, 3rd, 5th)', async ({ page }) => {
    // Login
    await page.goto('/');
    await page.locator('#user-name').fill('standard_user');
    await page.locator('#password').fill('secret_sauce');
    await page.locator('#login-button').click();
    
    const inventoryPage = new InventoryPage(page);
    await inventoryPage.waitForPageLoad();
    
    const cartPage = new CartPage(page);

    // Add items at indices 0, 2, 4 (Backpack, Bolt T-Shirt, Onesie)
    await inventoryPage.addProductToCart(0);
    await inventoryPage.addProductToCart(2);
    await inventoryPage.addProductToCart(4);

    await expect(inventoryPage.cartBadge.locator('.shopping_cart_badge')).toHaveText('3');

    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();

    const cartItems = await cartPage.getCartItemNames();
    expect(cartItems).toContain('Sauce Labs Backpack');
    expect(cartItems).toContain('Sauce Labs Bolt T-Shirt');
    expect(cartItems).toContain('Sauce Labs Onesie');
  });
});
