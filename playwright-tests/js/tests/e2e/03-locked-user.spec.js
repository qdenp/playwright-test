import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage";
import { users } from "../config";

/**
 * Locked out user sees the locked out error
 */
test("Locked out user sees the locked out error", async ({ page }) => {
  const loginPage = new LoginPage(page);

  // Use the credentials from the config
  const lockedUser = users.find((u) => u.username === "locked_out_user");

  await loginPage.goTo();
  await loginPage.login(lockedUser.username, lockedUser.password);

  await expect(loginPage.errorMessage).toBeVisible();
  const errorMessage = await loginPage.getErrorMessage();
  expect(errorMessage).toContain("locked out");
});
