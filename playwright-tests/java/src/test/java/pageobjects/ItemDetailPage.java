package pageobjects;

import config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
 * Page Object for the item detail page (inventory_details.html).
 */
public class ItemDetailPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By detailDescription = By.cssSelector(".inventory_details_desc");
    private final By backToProductsButton = By.cssSelector("[data-test='back-to-products']");

    public ItemDetailPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.DEFAULT_TIMEOUT));
    }

    /** Wait for the detail page to be fully rendered. */
    public void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(detailDescription));
    }

    /** Get the product description text on the detail page. */
    public String getDetailDescription() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(detailDescription)).getText();
    }

    /** Get the detail description element (for visibility checks). */
    public WebElement getDetailDescriptionLocator() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(detailDescription));
    }

    /** Click the "Back to Products" button. */
    public void clickBackToProducts() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(backToProductsButton));
        // Use JS click: the full-screen hamburger overlay (z-index:1) can
        // intercept Selenium's coordinate-based click.
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "arguments[0].click();", btn);
    }

    /** Get the "Back to Products" button locator (for visibility checks). */
    public WebElement getBackToProductsLocator() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(backToProductsButton));
    }
}
