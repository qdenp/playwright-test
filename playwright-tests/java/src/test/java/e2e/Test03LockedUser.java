package e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.LoginPage;

/**
 * Locked out user sees the locked out error.
 */
public class Test03LockedUser extends BaseTest {

    @Test
    @DisplayName("Locked out user sees the locked out error")
    void lockedOutUserSeesLockedOutError() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.open();
        loginPage.login(TestConfig.LOCKED_OUT_USER, TestConfig.passwordOf(TestConfig.LOCKED_OUT_USER));

        loginPage.waitForErrorMessage(TestConfig.DEFAULT_TIMEOUT);
        String error = loginPage.getErrorMessage();
        assertTrue(error.contains(TestConfig.ERROR_LOCKED_OUT), "the error must name the locked out account: " + error);
    }
}