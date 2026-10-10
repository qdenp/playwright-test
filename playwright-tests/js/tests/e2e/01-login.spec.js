import { test, expect } from '../fixtures.js';

/**
 * Успешный логин — стандартный пользователь
 */
test('Успешный логин — стандартный пользователь', async ({ authenticatedPage }) => {
  const { page, inventoryPage } = authenticatedPage;

  await expect(page).toHaveURL(/.*inventory\.html/);
  await expect(inventoryPage.headerTitle).toHaveText('Products');
  await expect(inventoryPage.products.first()).toBeVisible();
});
