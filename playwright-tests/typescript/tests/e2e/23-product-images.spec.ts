/**
 * Test 23: Verify product images are displayed
 */
import { test, expect } from '../fixtures.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';

test('23 - should display all product images', async ({ authenticatedPage }) => {
  const { page, inventoryPage } = authenticatedPage;

  // Verify all 6 product images are visible
  const productImages = page.locator('.inventory_item img');
  await expect(productImages).toHaveCount(6);

  // Verify each image has src attribute
  const images = await productImages.all();
  for (const img of images) {
    const src = await img.getAttribute('src');
    expect(src).toBeTruthy();
    expect(src!.length).toBeGreaterThan(0);
  }
});
