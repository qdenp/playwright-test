import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";

/**
 * Test 02: Неверный пароль — ошибка авторизации
 */
test("02 - should show error with invalid password", async ({ page }) => {
  const loginPage = new LoginPage(page);

  await loginPage.goTo();
  await loginPage.login("standard_user", "wrong_password");

  // Verify error message is visible
  await loginPage.waitForErrorMessage();
  const errorMessage = await loginPage.getErrorMessage();
  expect(errorMessage).toContain("do not match");
});
