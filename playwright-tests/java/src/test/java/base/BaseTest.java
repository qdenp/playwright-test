package base;

import config.TestConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pageobjects.CartPage;
import pageobjects.CheckoutPage;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

/**
 * Lifecycle shared by every test: a fresh headless Chrome session per test
 * plus the small navigation / authentication helpers used by the scenarios.
 *
 * Authentication is never performed in {@code @BeforeEach}: every test logs in
 * on its own, so the tests stay independent and can run in parallel.
 */
public abstract class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    private Path downloadDirectory;

    @BeforeEach
    void setUp() {
        downloadDirectory = createDownloadDirectory();
        ChromeOptions options = buildChromeOptions(downloadDirectory);
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TestConfig.NAVIGATION_TIMEOUT));
        wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.DEFAULT_TIMEOUT));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        deleteQuietly(downloadDirectory);
    }

    /** Directory headless Chrome downloads files into during the current test. */
    protected Path downloadDirectory() {
        return downloadDirectory;
    }

    /** Explicit wait with a custom budget. */
    protected WebDriverWait waitFor(int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    /**
     * Click an element via JavaScript. Selenium's coordinate-based click
     * ({@link org.openqa.selenium.interactions.Actions} or
     * {@link WebElement#click()}) cannot reach the full-screen
     * {@code #react-burger-menu-btn} overlay in the React SPA because the
     * button carries {@code z-index: 1} and sits behind page content.
     * A DOM-level JavaScript click bypasses z-index/occlusion entirely.
     */
    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Wait until a {@code By} locator resolves to a single element that is
     * visible according to JavaScript, not Selenium's {@code isDisplayed()}.
     *
     * <p>ChromeDriver 155 returns {@code false} for {@code isDisplayed()} on
     * overlay-style buttons ({@code #react-burger-menu-btn},
     * {@code #react-burger-cross-btn}) even when they are clearly visible
     * ({@code offsetParent}, {@code visibility}, {@code opacity} all healthy).
     * This helper uses the same JavaScript checks that the TS / Playwright
     * suite relies on.</p>
     */
    protected WebElement waitForJsVisible(By locator, int timeoutSeconds) {
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return w.until(d -> {
            try {
                WebElement el = d.findElement(locator);
                JavascriptExecutor js = (JavascriptExecutor) d;
                Boolean visible = (Boolean) js.executeScript(
                    "var el = arguments[0];"
                    + "return el.offsetParent !== null"
                    + "  && window.getComputedStyle(el).visibility !== 'hidden'"
                    + "  && window.getComputedStyle(el).display !== 'none'"
                    + "  && parseFloat(window.getComputedStyle(el).opacity) > 0"
                    + ";",
                    el);
                return visible != null && visible ? el : null;
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * Recreate the browser inside a running test.
     *
     * A second login within the same Chrome session is not reliable: Chrome
     * restores the login page from its cache and the keystrokes no longer
     * reach the form fields, so the login silently does nothing (the
     * reference suites re-login without this problem). Scenarios that walk
     * through several users give every user a fresh browser instead.
     */
    protected void restartBrowser() {
        driver.quit();
        driver = new ChromeDriver(buildChromeOptions(downloadDirectory));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TestConfig.NAVIGATION_TIMEOUT));
        wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.DEFAULT_TIMEOUT));
    }

    /**
     * Wait until headless Chrome has finished downloading a file into the
     * download directory of the current test.
     *
     * Chrome writes {@code <name>.crdownload} first and renames the file when it
     * is complete, so a file that does not end in that suffix and is not empty
     * is the finished download.
     */
    protected Path waitForDownloadedFile(int seconds) {
        return waitFor(seconds).until(d -> {
            try (var files = Files.list(downloadDirectory())) {
                return files
                    .filter(path -> {
                        String name = path.getFileName().toString();
                        return !name.endsWith(".crdownload") && isNotEmpty(path);
                    })
                    .findFirst()
                    .orElse(null);
            } catch (IOException e) {
                throw new IllegalStateException("Cannot read the download directory", e);
            }
        });
    }

    private boolean isNotEmpty(Path path) {
        try {
            return Files.size(path) > 0;
        } catch (IOException e) {
            return false;
        }
    }

    /** Open a path of the application under test, e.g. {@code cart.html}. */
    protected void openPath(String path) {
        driver.get(TestConfig.BASE_URL + path);
    }

    /** Login with the given credentials and wait for the inventory page. */
    protected InventoryPage loginAs(String username, String password) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.loginToInventory(username, password);
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.waitForPageLoad();
        return inventoryPage;
    }

    /** Login with the standard user and wait for the inventory page. */
    protected InventoryPage loginAsStandardUser() {
        return loginAs(TestConfig.USERNAME, TestConfig.PASSWORD);
    }

    /** Change the viewport of the browser window, e.g. 375x667 for a phone. */
    protected void setViewport(int width, int height) {
        driver.manage().window().setSize(new Dimension(width, height));
    }

    /** Handles of all currently open windows / tabs. */
    private ChromeOptions buildChromeOptions(Path downloads) {
        ChromeOptions options = new ChromeOptions();
        if (!TestConfig.CHROME_BINARY.isEmpty()) {
            options.setBinary(TestConfig.CHROME_BINARY);
        }
        options.addArguments(
            "--headless=new",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--window-size=1920,1080",
            "--disable-gpu",
            // Allow target="_blank" links to open new tabs in headless Chrome
            "--disable-popup-blocking",
            // The CI images reach the site through a TLS-inspecting proxy whose CA
            // lives in the system trust store; Chrome keeps its own store, so the
            // certificate errors of that proxy have to be tolerated.
            "--ignore-certificate-errors");

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloads.toAbsolutePath().toString());
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("safebrowsing.enabled", true);
        options.setExperimentalOption("prefs", prefs);
        return options;
    }

    private Path createDownloadDirectory() {
        try {
            return Files.createTempDirectory("saucedemo-downloads-" + UUID.randomUUID());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create the download directory", e);
        }
    }

    private void deleteQuietly(Path directory) {
        if (directory == null || !Files.exists(directory)) {
            return;
        }
        try (var walk = Files.walk(directory)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                    // cleanup only
                }
            });
        } catch (IOException ignored) {
            // cleanup only
        }
    }
}
