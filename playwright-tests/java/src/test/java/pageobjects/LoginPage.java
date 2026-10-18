package pageobjects;

import config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

/**
 * Page Object for the login page.
 */
public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector(".error-message-container");
    private final By hamburgerMenu = By.id("react-burger-menu-btn");
    private final By closeButton = By.id("react-burger-cross-btn");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.DEFAULT_TIMEOUT));
    }

    public void open() {
        driver.get(TestConfig.BASE_URL);
        // Wait for login form to be ready
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
    }

    public void enterUsername(String username) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        element.clear();
        element.sendKeys(username);
    }

    public void enterPassword(String password) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        element.sendKeys(password);
    }

    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    /**
     * Fill credentials and click login. Does NOT wait for navigation.
     * Use this for failed login scenarios where the page stays on login.
     */
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    /**
     * Fill credentials and click login, then wait for navigation to inventory page.
     * Use this for successful login scenarios.
     */
    public void loginToInventory(String username, String password) {
        login(username, password);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.NAVIGATION_TIMEOUT));
        wait.until(ExpectedConditions.urlContains("inventory.html"));
    }

    /** Login with default credentials and wait for inventory page. */
    public void login() {
        loginToInventory(TestConfig.USERNAME, TestConfig.PASSWORD);
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
    }

    public boolean isErrorMessageVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLoginFormVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
            wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
            wait.until(ExpectedConditions.visibilityOfElementLocated(loginButton));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForErrorMessage(int timeoutSeconds) {
        new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
            .until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
    }

    public void openHamburgerMenu() {
        WebElement hamburger = wait.until(ExpectedConditions.presenceOfElementLocated(hamburgerMenu));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", hamburger);
        // Selenium's isDisplayed() returns false for overlay buttons in Chrome 155;
        // wait for the menu to open via aria-hidden on bm-menu-wrap.
        wait.until(d -> {
            try {
                String hidden = (String) ((org.openqa.selenium.JavascriptExecutor) d).executeScript(
                    "var el = document.querySelector('.bm-menu-wrap');"
                    + "return el !== null ? el.getAttribute('aria-hidden') : null;");
                return "false".equals(hidden);
            } catch (Exception e) {
                return false;
            }
        });
    }

    public void closeHamburgerMenu() {
        try {
            String hidden = (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "var el = document.querySelector('.bm-menu-wrap');"
                + "return el !== null ? el.getAttribute('aria-hidden') : null;");
            if ("false".equals(hidden)) {
                WebElement closeBtn = driver.findElement(closeButton);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", closeBtn);
                wait.until(d -> {
                    try {
                        String h = (String) ((org.openqa.selenium.JavascriptExecutor) d).executeScript(
                            "var el = document.querySelector('.bm-menu-wrap');"
                            + "return el !== null ? el.getAttribute('aria-hidden') : null;");
                        return "true".equals(h);
                    } catch (Exception e) {
                        return false;
                    }
                });
            }
        } catch (Exception e) {
            // Menu might already be closed
        }
    }

    public boolean isHamburgerMenuOpen() {
        try {
            return (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "var el = document.querySelector('.bm-menu-wrap');"
                + "return el !== null && el.getAttribute('aria-hidden') === 'false';");
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getMenuItems() {
        List<WebElement> items = driver.findElements(By.cssSelector(".bm-item-list a"));
        List<String> result = new ArrayList<>();
        for (WebElement item : items) {
            result.add(item.getText().trim());
        }
        return result;
    }
}