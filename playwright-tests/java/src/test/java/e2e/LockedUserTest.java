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
public class LockedUserTest extends BaseTest {

    @Test
    @DisplayName("Locked out user sees the locked out error")
    void lockedOutUserSeesLockedOutError() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.open();
        loginPage.login("locked_out_user", TestConfig.passwordOf("locked_out_user"));

        loginPage.waitForErrorMessageVisible();
        String error = loginPage.getErrorMessageText();
        assertTrue(error.contains("locked out"), "the error must name the locked out account: " + error);
    }
}
