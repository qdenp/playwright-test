package e2e;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * About page - navigating from the hamburger menu.
 *
 * SAUCE_DEMO_BUG: the About link leads to saucelabs.com, which returns
 * 403 Forbidden. The redirect and the About page content cannot be verified,
 * so the test stays disabled - exactly like the reference suites.
 *
 * Once the SauceDemo-side bug is fixed: remove {@code @Disabled} and restore the
 * body: open the login page, log in, open the hamburger menu, click About and
 * assert that the browser leaves the shop.
 */
@Disabled("SAUCE_DEMO_BUG: the About link leads to saucelabs.com, which returns 403 Forbidden")
public class AboutPageTest {

    @Test
    @DisplayName("About page - navigating from the hamburger menu")
    void aboutPageNavigation() {
        // Nothing to verify while saucelabs.com answers 403.
    }
}
