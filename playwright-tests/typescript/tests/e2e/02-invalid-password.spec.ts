import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";

/**
 * Invalid password shows an authentication error
 */
test("Invalid password shows an authentication error", async ({ page }) => {
  const loginPage = new LoginPage(page);

  await loginPage.goTo();
  await loginPage.login("standard_user", "wrong_password");

  // Verify error message is visible
  await loginPage.waitForErrorMessage();
  const errorMessage = await loginPage.getErrorMessage();
  expect(errorMessage).toContain("do not match");
});
