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
public class Test02InvalidPassword extends BaseTest {

    @Test
    @DisplayName("Invalid password shows an authentication error")
    void invalidPasswordShowsAuthenticationError() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.open();
        loginPage.login(TestConfig.USERNAME, TestConfig.WRONG_PASSWORD);

        loginPage.waitForErrorMessage(TestConfig.DEFAULT_TIMEOUT);
        String error = loginPage.getErrorMessage();
        assertTrue(error.contains(TestConfig.ERROR_PASSWORD_MISMATCH), "the error must name the mismatching credentials: " + error);
    }
}