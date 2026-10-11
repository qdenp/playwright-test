import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { users } from "../config.js";

/**
 * Блокированный пользователь — locked_out_user
 */
test("Блокированный пользователь — locked_out_user", async ({ page }) => {
  const loginPage = new LoginPage(page);

  // Используем credentials из config
  const lockedUser = users.find((u) => u.username === "locked_out_user");

  await loginPage.goTo();
  await loginPage.login(lockedUser.username, lockedUser.password);

  await expect(loginPage.errorMessage).toBeVisible();
  const errorMessage = await loginPage.getErrorMessage();
  expect(errorMessage).toContain("locked out");
});
