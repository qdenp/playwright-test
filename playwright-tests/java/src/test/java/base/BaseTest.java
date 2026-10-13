package base;

import config.TestConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;

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

    /** Open a path of the application under test, e.g. {@code /cart.html}. */
    protected void openPath(String path) {
        driver.get(TestConfig.BASE_URL + path);
    }

    /** Login with the given credentials and wait for the inventory page. */
    protected InventoryPage loginAs(String username, String password) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.login(username, password);
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.waitForPageLoad(TestConfig.SLOW_TIMEOUT);
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

    /** Wait until the browser reports the given number of open windows / tabs. */
    protected void waitForWindowCount(int expected) {
        waitFor(TestConfig.DEFAULT_TIMEOUT)
            .until(d -> d.getWindowHandles().size() == expected);
    }

    /**
     * Switch to the window that is not {@code currentHandle} and return its handle.
     * Used by the scenarios that open a link in a new tab.
     */
    protected String switchToOtherWindow(String currentHandle) {
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(currentHandle)) {
                driver.switchTo().window(handle);
                return handle;
            }
        }
        throw new IllegalStateException("No window other than " + currentHandle + " is open: " + handles);
    }

    /** Handles of all currently open windows / tabs. */
    private ChromeOptions buildChromeOptions(Path downloads) {
        ChromeOptions options = new ChromeOptions();
        options.setBinary(TestConfig.CHROME_BINARY);
        options.addArguments(
            "--headless=new",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--window-size=1920,1080",
            "--disable-gpu",
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
