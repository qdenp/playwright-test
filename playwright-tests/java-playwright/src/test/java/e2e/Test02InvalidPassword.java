package e2e;

import com.microsoft.playwright.Page;
import config.TestConfig;
import pageobjects.LoginPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Invalid password shows an authentication error
 */
class Test02InvalidPassword extends BaseTest {

    @Test
    void invalidPasswordShowsAnAuthenticationError(Page page) {
        LoginPage loginPage = new LoginPage(page);

        loginPage.goTo();
        loginPage.login("standard_user", "wrong_password");

        // Verify error message is visible
        loginPage.waitForErrorMessage();
        String errorMessage = loginPage.getErrorMessage();
        Assertions.assertTrue(errorMessage.contains("do not match"));
    }
}
