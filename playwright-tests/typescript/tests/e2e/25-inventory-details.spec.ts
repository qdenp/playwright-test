/**
 * Test 25: Verify inventory item details
 */
import { test, expect } from '../fixtures.js';
import { LoginPage } from '../page-objects/LoginPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';

test('25 - should display all inventory item details', async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Verify each product has name, description, price, and add to cart button
  const products = await inventoryPage.products.all();
  expect(products.length).toBe(6);

  for (let i = 0; i < products.length; i++) {
    const product = products[i];

    // Verify product has name
    const name = await product.locator('.inventory_item_name').textContent();
    expect(name).toBeTruthy();
    expect(name!.length).toBeGreaterThan(0);

    // Verify product has description
    const desc = await product
      .locator('.inventory_item_desc')
      .textContent();
    expect(desc).toBeTruthy();

    // Verify product has price
    const price = await product
      .locator('.inventory_item_price')
      .textContent();
    expect(price).toBeTruthy();
    expect(price).toContain('$');

    // Verify add to cart button is visible
    await expect(
      product.locator('.btn.btn_primary.btn_small.btn_inventory'),
    ).toBeVisible();
  }
});
