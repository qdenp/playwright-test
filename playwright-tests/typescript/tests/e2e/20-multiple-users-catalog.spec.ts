import { test, expect } from '../fixtures.js';
import { LoginPage } from '../page-objects/LoginPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';
import { User } from '../config.js';

/**
 * Тест 20: Разные пользователи видят одинаковый каталог
 */
test('Тест 20: Разные пользователи видят одинаковый каталог', async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  // Берём первых 5 пользователей
  const testUsers: User[] = [
    { username: 'standard_user', password: 'secret_sauce' },
    { username: 'problem_user', password: 'secret_sauce' },
    { username: 'performance_glitch_user', password: 'secret_sauce' },
    { username: 'error_user', password: 'secret_sauce' },
    { username: 'visual_user', password: 'secret_sauce' },
  ];

  for (const { username, password } of testUsers) {
    await loginPage.goTo();
    await loginPage.login(username, password);
    await inventoryPage.waitForPageLoad();

    // Все пользователи видят 6 товаров
    await expect(inventoryPage.products).toHaveCount(6);

    // Все видят одинаковые названия товаров
    const names = await inventoryPage.getAllProductNames();
    expect(names).toContain('Sauce Labs Backpack');
    expect(names).toContain('Sauce Labs Bike Light');
  }
});
