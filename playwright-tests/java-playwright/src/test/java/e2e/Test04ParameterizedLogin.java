package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import base.BaseTest;

/**
 * Parametrise logins with test.each
 */
class Test04ParameterizedLogin extends BaseTest {

    @ParameterizedTest(name = "Login: {0} - {1}")
    @MethodSource("getLoginTestCases")
    void loginTest(String username, String password, boolean shouldLogin, String expectedTitle, String expectedError, Page page) {
        LoginPage loginPage = new LoginPage(page);
        InventoryPage inventoryPage = new InventoryPage(page);

        loginPage.goTo();

        if (shouldLogin) {
            loginPage.login(username, password);

            // A successful login leaves the login form behind and shows the catalogue.
            // performance_glitch_user needs the longer budget.
            inventoryPage.waitForPageLoad();
            Assertions.assertEquals(expectedTitle, inventoryPage.getTitleText());
        } else {
            // A failed login stays on the login form and explains why
            loginPage.login(username, password);
            Assertions.assertTrue(loginPage.getUsernameInput().isVisible());

            String errorMessage = loginPage.getErrorMessage();
            Assertions.assertTrue(errorMessage.contains(expectedError));
            Assertions.assertFalse(inventoryPage.getHeaderTitle().isVisible());
        }
    }

    static Object[][] getLoginTestCases() {
        return new Object[][]{
                {"standard_user", "secret_sauce", true, "Products", null},
                {"problem_user", "secret_sauce", true, "Products", null},
                {"performance_glitch_user", "secret_sauce", true, "Products", null},
                {"error_user", "secret_sauce", true, "Products", null},
                {"visual_user", "secret_sauce", true, "Products", null},
                {"locked_out_user", "secret_sauce", false, "Products", "locked out"},
                {"standard_user", "wrong_password", false, "Products", "Username and password do not match"},
                {"", "secret_sauce", false, "Products", "Username is required"}
        };
    }
}
