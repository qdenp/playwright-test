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
 * Page Object for the cart page.
 */
public class CartPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public final By cartItems = By.cssSelector(".cart_item");
    private final By checkoutButton = By.cssSelector(".checkout_button");
    private final By continueShoppingButton = By.cssSelector("[data-test='continue-shopping']");
    private final By itemNames = By.cssSelector(".inventory_item_name");
    private final By itemPrices = By.cssSelector(".inventory_item_price");
    private final By itemQuantities = By.cssSelector(".cart_quantity");
    private final By removeButtons = By.cssSelector(".cart_button");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.DEFAULT_TIMEOUT));
    }

    public void waitForPageLoad() {
        // SPA may redirect after removing items - just wait for DOM
        wait.until(d -> d.getPageSource() != null);
    }

    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }

    public void waitForCartItemCount(int expectedCount) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.DEFAULT_TIMEOUT));
        wait.until(d -> {
            // Use JavaScript to get fresh DOM count (Selenium caches element lists)
            long count = (Long) ((org.openqa.selenium.JavascriptExecutor) d)
                .executeScript("return document.querySelectorAll('.cart_item').length;");
            return count == expectedCount;
        });
    }

    public List<String> getCartItemNames() {
        List<String> names = new ArrayList<>();
        List<WebElement> elements = driver.findElements(itemNames);
        for (WebElement element : elements) {
            names.add(element.getText().trim());
        }
        return names;
    }

    public List<Double> getCartItemPrices() {
        List<Double> prices = new ArrayList<>();
        List<WebElement> elements = driver.findElements(itemPrices);
        for (WebElement element : elements) {
            String text = element.getText();
            if (text != null && !text.isEmpty()) {
                prices.add(Double.parseDouble(text.replace("$", "").replace(",", "")));
            }
        }
        return prices;
    }

    public List<Integer> getCartItemQuantities() {
        List<Integer> quantities = new ArrayList<>();
        List<WebElement> elements = driver.findElements(itemQuantities);
        for (WebElement element : elements) {
            String text = element.getText();
            if (text != null && !text.isEmpty()) {
                quantities.add(Integer.parseInt(text));
            }
        }
        return quantities;
    }

    public void removeItem(int index) {
        List<WebElement> items = driver.findElements(cartItems);
        if (index < items.size()) {
            // Match TypeScript: button:has-text("Remove")
            WebElement removeBtn = items.get(index).findElement(
                By.xpath(".//button[contains(text(), 'Remove')]"));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", removeBtn);
        }
    }

    public void continueShopping() {
        // Use JavaScript click for React SPA (more reliable than Selenium click)
        WebElement btn = driver.findElement(By.cssSelector("[data-test='continue-shopping']"));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        // Wait for SPA navigation
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.NAVIGATION_TIMEOUT));
        longWait.until(d -> {
            String url = d.getCurrentUrl();
            return url.contains("inventory.html") || url.contains("inventory");
        });
    }

    public void checkout() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(checkoutButton));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }
}