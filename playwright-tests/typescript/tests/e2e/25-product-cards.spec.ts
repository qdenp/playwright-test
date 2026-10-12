/**
 * Тест 25: Проверка карточек товаров на главной
 */
import { test, expect } from '../fixtures.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';

test('Проверка карточек товаров на главной', async ({ authenticatedPage }) => {
  const { inventoryPage } = authenticatedPage;

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
