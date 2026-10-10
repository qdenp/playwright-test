import { test, expect } from '../fixtures.js';
import { LoginPage } from '../page-objects/LoginPage.js';
import { InventoryPage } from '../page-objects/InventoryPage.js';
import { loginTests } from '../config.js';

/**
 * Параметризация логинов через test.each
 */
loginTests.forEach(({ username, password, shouldLogin, expectedTitle, expectedError }) => {
  const status = shouldLogin ? 'success' : 'failure';
  test(`Тест входа: ${username} — ${status}`, { timeout: 60000 }, async ({ page }) => {
    const loginPage = new LoginPage(page);
    const inventoryPage = new InventoryPage(page);

    await loginPage.goTo();
    await loginPage.login(username, password);

    if (shouldLogin) {
      await expect(page).toHaveURL(/.*saucedemo/);
      await inventoryPage.waitForPageLoad();
      const title = await inventoryPage.getTitleText();
      expect(title).toBe(expectedTitle);
    } else {
      await expect(page).toHaveURL(/.*saucedemo/);
      await loginPage.waitForErrorMessage();
      const errorMessage = await loginPage.getErrorMessage();
      expect(errorMessage).toContain(expectedError);
    }
  });
});
