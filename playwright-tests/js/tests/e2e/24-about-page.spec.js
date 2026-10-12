import { test } from "../fixtures.js";

/**
 * About page — переход из hamburger меню
 *
 * SAUCE_DEMO_BUG: ссылка About ведёт на saucelabs.com, который отдаёт
 * 403 Forbidden. Проверить переход и содержимое страницы About невозможно,
 * поэтому тест помечен как skip.
 *
 * Когда баг на стороне SauceDemo исправят: убрать `test.skip`, восстановить
 * импорты LoginPage / InventoryPage и тело теста:
 *   loginPage.goTo() → loginPage.login() → loginPage.openHamburgerMenu()
 *   → inventoryPage.clickAbout() → expect(page).toHaveURL(...)
 */
test.skip("About page — переход из hamburger меню", async () => {
  // SAUCE_DEMO_BUG: About ведёт на saucelabs.com, который отдаёт 403 Forbidden.
});
