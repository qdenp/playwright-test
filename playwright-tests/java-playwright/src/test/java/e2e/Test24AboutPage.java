package e2e;

import org.junit.jupiter.api.*;

/**
 * About page - navigating from the hamburger menu
 *
 * SAUCE_DEMO_BUG: the About link leads to saucelabs.com, which returns
 * 403 Forbidden. The redirect and the About page content cannot be verified,
 * so the test is marked as skip.
 *
 * Once the SauceDemo-side bug is fixed: remove `test.skip`, restore the
 * LoginPage / InventoryPage imports and the test body:
 *   loginPage.goTo() -> loginPage.login() -> loginPage.openHamburgerMenu()
 *   -> inventoryPage.clickAbout() -> expect(page).toHaveURL(...)
 */
class Test24AboutPage {

    @Test
    @Disabled("SAUCE_DEMO_BUG: About leads to saucelabs.com, which returns 403 Forbidden.")
    void aboutPageNavigatingFromTheHamburgerMenu() {
        // SAUCE_DEMO_BUG: About leads to saucelabs.com, which returns 403 Forbidden.
    }
}
