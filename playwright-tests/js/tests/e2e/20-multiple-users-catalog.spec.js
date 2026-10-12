import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { EXPECTED_PRODUCT_COUNT } from "../config.js";

/**
 * Разные пользователи видят одинаковый каталог
 */
test("Разные пользователи видят одинаковый каталог", async ({
  page,
}) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  const testUsers = [
    { username: "standard_user", password: "secret_sauce" },
    { username: "problem_user", password: "secret_sauce" },
    { username: "performance_glitch_user", password: "secret_sauce" },
    { username: "error_user", password: "secret_sauce" },
    { username: "visual_user", password: "secret_sauce" },
  ];

  let baselineNames = null;

  for (const { username, password } of testUsers) {
    await loginPage.goTo();
    await loginPage.login(username, password);
    await inventoryPage.waitForPageLoad();

    // Все пользователи видят одинаковое число товаров с названиями
    await expect(inventoryPage.productNames).toHaveCount(EXPECTED_PRODUCT_COUNT);

    const names = await inventoryPage.getAllProductNames();

    if (baselineNames === null) {
      baselineNames = names;
    } else {
      expect(names).toEqual(baselineNames);
    }
  }
});
