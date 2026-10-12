import { test } from "../fixtures.js";

/**
 * About page - navigating from the hamburger menu
 *
 * SAUCE_DEMO_BUG: the About link leads to saucelabs.com, which returns
 * 403 Forbidden. The redirect and the About page content cannot be verified,
 * so the test is marked as skip.
 *
 * Once the SauceDemo-side bug is fixed: remove `test.skip`, restore the
 * LoginPage / InventoryPage imports and the test body:
 *   loginPage.goTo() → loginPage.login() → loginPage.openHamburgerMenu()
 *   → inventoryPage.clickAbout() → expect(page).toHaveURL(...)
 */
test.skip("About page - navigating from the hamburger menu", async () => {
  // SAUCE_DEMO_BUG: About leads to saucelabs.com, which returns 403 Forbidden.
});
