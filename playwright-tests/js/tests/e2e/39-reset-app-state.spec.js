/**
 * Test 39: Reset App State tests
 */
import { test, expect } from '../fixtures';
import { LoginPage } from '../page-objects/LoginPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';
import { CartPage } from '../page-objects/CartPage.js';

test.describe('Reset App State', () => {
  test('01 - should reset cart via Reset App State in hamburger menu', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);
    const cartPage = new CartPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    // Add products to cart
    await inventoryPage.addProductToCart(0);
    await inventoryPage.addProductToCart(1);
    await expect(inventoryPage.cartBadge.locator('.shopping_cart_badge')).toHaveText('2');

    // Reset app state via hamburger menu
    await inventoryPage.openHamburgerMenu();
    await inventoryPage.clickResetAppState();
    // Reload page to reflect the reset (Sauce Demo bug: cart UI doesn't update without reload)
    await page.reload();
    await inventoryPage.waitForPageLoad();

    // Cart should be empty
    const badgeCount = await inventoryPage.getCartBadgeCount();
    expect(badgeCount).toBe(0);
  });

  test('02 - should reset app state and verify complete reset', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);
    const cartPage = new CartPage(page);

    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    // Add products and navigate to cart
    await inventoryPage.addProductToCart(0);
    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();

    // Reset app state via hamburger menu
    await inventoryPage.openHamburgerMenu();
    await inventoryPage.clickResetAppState();
    // Reload page to reflect the reset (Sauce Demo bug: cart UI doesn't update without reload)
    await page.reload();
    // Sauce Demo redirects to login page after reset
    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();

    // Verify complete reset
    const badgeCount = await inventoryPage.getCartBadgeCount();
    expect(badgeCount).toBe(0);
    expect(await inventoryPage.getProductCount()).toBe(6);
  });
});
