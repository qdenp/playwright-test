package pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import config.TestConfig;

/**
 * Page Object for the login page.
 * Mirrors TypeScript LoginPage.ts
 */
public class LoginPage {
    private final Page page;

    // Locators
    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;

    public LoginPage(Page page) {
        this.page = page;
        this.usernameInput = page.locator("#user-name");
        this.passwordInput = page.locator("#password");
        this.loginButton = page.locator("#login-button");
        this.errorMessage = page.locator(".error-message-container");
    }

    public void goTo() {
        Page.NavigateOptions options = new Page.NavigateOptions()
                .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED)
                .setTimeout(TestConfig.NAVIGATION_TIMEOUT);
        page.navigate("", options);
    }

    public void enterUsername(String username) {
        usernameInput.clear();
        usernameInput.fill(username != null ? username : TestConfig.USERNAME);
    }

    public void enterPassword(String password) {
        passwordInput.clear();
        passwordInput.fill(password != null ? password : TestConfig.PASSWORD);
    }

    public void clickLogin() {
        loginButton.click();
    }

    public void login() {
        enterUsername(TestConfig.USERNAME);
        enterPassword(TestConfig.PASSWORD);
        clickLogin();
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public String getErrorMessage() {
        return errorMessage.innerText();
    }

    public boolean isErrorMessageVisible() {
        return errorMessage.isVisible();
    }

    public void waitForErrorMessage() {
        errorMessage.waitFor(new Locator.WaitForOptions()
                .setTimeout(TestConfig.DEFAULT_TIMEOUT));
    }

    public Locator getUsernameInput() {
        return usernameInput;
    }

    public Locator getLoginButton() {
        return loginButton;
    }

    public Locator getErrorMessageLocator() {
        return errorMessage;
    }
}
