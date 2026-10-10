import { test, expect } from '../fixtures.js';
import { LoginPage } from '../page-objects/LoginPage.js';
import { config } from '../config.js';

/**
 * Скриншот при падении — неверные данные
 */
test('Скриншот при падении — неверные данные', async ({ page }) => {
  const loginPage = new LoginPage(page);

  await loginPage.goTo();
  await loginPage.login(config.username, 'wrong_password');

  await expect(loginPage.errorMessage).toBeVisible();
  const errorMessage = await loginPage.getErrorMessage();
  expect(errorMessage).toContain('do not match');
});
