import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage.js";

/**
 * Test 03: Блокированный пользователь — locked_out_user
 */
test("03 - should show error for locked_out_user", async ({ page }) => {
  const loginPage = new LoginPage(page);

  await loginPage.goTo();
  await loginPage.login("locked_out_user", "secret_sauce");

  // Verify error message is visible
  await loginPage.waitForErrorMessage();
  const errorMessage = await loginPage.getErrorMessage();
  expect(errorMessage).toContain("locked out");
});
