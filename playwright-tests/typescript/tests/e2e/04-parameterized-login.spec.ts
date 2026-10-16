import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage";
import { InventoryPage } from "../page-objects/InventoryPage";
import { loginTests, timeouts, LoginTestParams } from "../config";

/**
 * Parametrise logins with test.each
 */
loginTests.forEach(
  ({
    username,
    password,
    shouldLogin,
    expectedTitle,
    expectedError,
  }: LoginTestParams) => {
    const status = shouldLogin ? "success" : "failure";
    test(`Login: ${username} - ${status}`, async ({ page }) => {
      const loginPage = new LoginPage(page);
      const inventoryPage = new InventoryPage(page);

      await loginPage.goTo();

      if (shouldLogin) {
        await loginPage.login(username, password);

        // A successful login leaves the login form behind and shows the catalogue.
        // performance_glitch_user needs the longer budget.
        await expect(inventoryPage.headerTitle).toHaveText(expectedTitle, {
          timeout: timeouts.SLOW,
        });
      } else {
        // A failed login stays on the login form and explains why
        await loginPage.login(username, password);
        await expect(loginPage.usernameInput).toBeVisible();

        await expect(loginPage.errorMessage).toContainText(expectedError!);
        await expect(inventoryPage.headerTitle).toBeHidden();
      }
    });
  },
);
