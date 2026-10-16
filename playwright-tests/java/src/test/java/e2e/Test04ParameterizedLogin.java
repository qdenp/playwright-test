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
public class Test04ParameterizedLogin extends BaseTest {

    static Stream<Arguments> loginCases() {
        return Stream.of(
            Arguments.of(TestConfig.LOGIN_TEST_CASES[0]),
            Arguments.of(TestConfig.LOGIN_TEST_CASES[1]),
            Arguments.of(TestConfig.LOGIN_TEST_CASES[2]),
            Arguments.of(TestConfig.LOGIN_TEST_CASES[3]),
            Arguments.of(TestConfig.LOGIN_TEST_CASES[4]),
            Arguments.of(TestConfig.LOGIN_TEST_CASES[5]),
            Arguments.of(TestConfig.LOGIN_TEST_CASES[6]),
            Arguments.of(TestConfig.LOGIN_TEST_CASES[7]));
    }

    @ParameterizedTest(name = "{0} with the given password: login {2}")
    @MethodSource("loginCases")
    @DisplayName("Login with every user type and with malformed credentials")
    void login(String username, String password, boolean shouldLogin, String expectedTitle, String expectedError) {
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = new InventoryPage(driver);

        loginPage.open();

        if (shouldLogin) {
            loginPage.loginToInventory(username, password);
            inventoryPage.waitForPageLoad();
            assertEquals(TestConfig.INVENTORY_PAGE_TITLE, inventoryPage.getTitleText());
        } else {
            loginPage.login(username, password);

            // A failed login stays on the login form and explains why.
            loginPage.waitForErrorMessage(TestConfig.DEFAULT_TIMEOUT);
            String error = loginPage.getErrorMessage();
            assertTrue(error.contains(expectedError), "error mentions: " + expectedError);

            assertFalse(inventoryPage.isProductsVisible(), "the catalogue is never rendered");
        }
    }
}