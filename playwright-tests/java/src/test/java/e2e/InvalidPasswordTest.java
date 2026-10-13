package e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.LoginPage;

/**
 * Invalid password shows an authentication error.
 */
public class InvalidPasswordTest extends BaseTest {

    @Test
    @DisplayName("Invalid password shows an authentication error")
    void invalidPasswordShowsAuthenticationError() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.open();
        loginPage.login(TestConfig.USERNAME, "wrong_password");

        loginPage.waitForErrorMessageVisible();
        String error = loginPage.getErrorMessageText();
        assertTrue(error.contains("do not match"), "the error must name the mismatching credentials: " + error);
    }
}
