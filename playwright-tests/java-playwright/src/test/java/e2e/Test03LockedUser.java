package e2e;

import com.microsoft.playwright.Page;
import pageobjects.LoginPage;
import org.junit.jupiter.api.*;
import base.BaseTest;

/**
 * Locked out user sees the locked out error
 */
class Test03LockedUser extends BaseTest {

    @Test
    void lockedOutUserSeesTheLockedOutError(Page page) {
        LoginPage loginPage = new LoginPage(page);

        loginPage.goTo();
        loginPage.login("locked_out_user", "secret_sauce");

        // Verify error message is visible
        loginPage.waitForErrorMessage();
        String errorMessage = loginPage.getErrorMessage();
        Assertions.assertTrue(errorMessage.contains("locked out"));
    }
}
