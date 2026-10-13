package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;

/**
 * Parametrised logins: every known user plus the two malformed credential pairs.
 *
 * Cases where the login is expected to fail stay on the login form and render an
 * error; successful ones reach the catalogue. performance_glitch_user logs in
 * deliberately slowly, so the successful cases are given the long budget.
 */
public class ParameterizedLoginTest extends BaseTest {

    static Stream<Arguments> loginCases() {
        return Stream.of(
            Arguments.of("standard_user", "secret_sauce", true, "Products", null),
            Arguments.of("problem_user", "secret_sauce", true, "Products", null),
            Arguments.of("performance_glitch_user", "secret_sauce", true, "Products", null),
            Arguments.of("error_user", "secret_sauce", true, "Products", null),
            Arguments.of("visual_user", "secret_sauce", true, "Products", null),
            Arguments.of("locked_out_user", "secret_sauce", false, null, "locked out"),
            Arguments.of("standard_user", "wrong_password", false, null, "do not match"),
            Arguments.of("", "secret_sauce", false, null, "Username is required"));
    }

    @ParameterizedTest(name = "{0} with the given password: login {2}")
    @MethodSource("loginCases")
    @DisplayName("Login with every user type and with malformed credentials")
    void login(String username, String password, boolean shouldLogin, String expectedTitle, String expectedError) {
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = new InventoryPage(driver);

        loginPage.open();

        if (shouldLogin) {
            loginPage.login(username, password);
            inventoryPage.waitForPageLoad(TestConfig.SLOW_TIMEOUT);
            assertEquals(expectedTitle, inventoryPage.getTitleText());
        } else {
            loginPage.login(username, password);

            // A failed login stays on the login form and explains why.
            loginPage.waitForUsernameInputVisible();
            assertTrue(loginPage.isUsernameInputVisible(), "the login form stays on screen");
            loginPage.waitForErrorMessageText(expectedError);

            waitFor(TestConfig.DEFAULT_TIMEOUT)
                .until(d -> inventoryPage.isHeaderTitleHidden());
            assertFalse(inventoryPage.isHeaderTitleVisible(), "the catalogue is never rendered");
        }
    }
}
