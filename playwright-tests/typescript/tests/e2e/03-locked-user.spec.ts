import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";

/**
 * Locked out user sees the locked out error
 */
test("Locked out user sees the locked out error", async ({ page }) => {
  const loginPage = new LoginPage(page);

  await loginPage.goTo();
  await loginPage.login("locked_out_user", "secret_sauce");

  // Verify error message is visible
  await loginPage.waitForErrorMessage();
  const errorMessage = await loginPage.getErrorMessage();
  expect(errorMessage).toContain("locked out");
});
