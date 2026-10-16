package pageobjects;

import config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

/**
 * Page Object for the checkout pages (step one, step two, complete).
 */
public class CheckoutPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By zipCodeInput = By.id("postal-code");
    private final By continueButton = By.cssSelector("[data-test='continue']");
    private final By finishButton = By.cssSelector("[data-test='finish']");
    private final By cancelButton = By.cssSelector("[data-test='cancel']");
    private final By errorMessage = By.cssSelector(".error-message-container");
    private final By completeTitle = By.cssSelector(".complete-header");
    private final By backToProductsButton = By.cssSelector(".btn.btn_action.btn_small");
    private final By summarySubtotalLabel = By.cssSelector(".summary_subtotal_label");
    private final By summaryTaxLabel = By.cssSelector(".summary_tax_label");
    private final By summaryTotalLabel = By.cssSelector(".summary_total_label");
    public final By summaryItemPrices = By.cssSelector(".cart_item .inventory_item_price");
    private final By generatePdfButton = By.cssSelector("[data-test='generate-pdf-order']");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.DEFAULT_TIMEOUT));
    }

    public void waitForPageLoad() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.NAVIGATION_TIMEOUT));
        longWait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("checkout-step-one.html"),
            ExpectedConditions.urlContains("checkout-step-two.html"),
            ExpectedConditions.urlContains("checkout-complete.html")
        ));
    }

    public void fillFirstName(String firstName) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput));
        element.clear();
        element.sendKeys(firstName);
    }

    public void fillLastName(String lastName) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(lastNameInput));
        element.clear();
        element.sendKeys(lastName);
    }

    public void fillZipCode(String zipCode) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(zipCodeInput));
        element.clear();
        element.sendKeys(zipCode);
    }

    public void clickContinue() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(continueButton));
        jsClickSafe(btn);
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.NAVIGATION_TIMEOUT));
        longWait.until(ExpectedConditions.visibilityOfElementLocated(finishButton));
    }

    /**
     * Click Continue button without waiting for navigation (for validation steps).
     * Use this when the form is expected to show validation errors and stay on the same page.
     */
    public void clickContinueExpectingValidation() {
        final String previousError = getErrorMessageTextSafe();

        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(continueButton));
        jsClickSafe(btn);

        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.DEFAULT_TIMEOUT));
        longWait.until(d -> {
            try {
                String text = d.findElement(errorMessage).getText().trim();
                return (text.isEmpty() || text.equals(previousError)) ? null : d.findElement(errorMessage);
            } catch (Exception e) {
                return null;
            }
        });
    }

    private String getErrorMessageTextSafe() {
        try {
            String text = driver.findElement(errorMessage).getText().trim();
            return text != null ? text : "";
        } catch (Exception e) {
            return "";
        }
    }

    public void clickFinish() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(finishButton));
        jsClickSafe(btn);
    }

    public void clickCancel() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(cancelButton));
        jsClickSafe(btn);
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

    public String getCompleteTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(completeTitle)).getText();
    }

    public void clickBackToProducts() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(backToProductsButton));
        jsClickSafe(btn);
    }

    public double getSubtotal() {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(summarySubtotalLabel)).getText();
        return parseMoney(text);
    }

    public double getTax() {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(summaryTaxLabel)).getText();
        return parseMoney(text);
    }

    public double getTotal() {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(summaryTotalLabel)).getText();
        return parseMoney(text);
    }

    public List<Double> getSummaryItemPrices() {
        List<Double> prices = new ArrayList<>();
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.DEFAULT_TIMEOUT));
        // Wait for at least one price element to be visible
        longWait.until(ExpectedConditions.visibilityOfElementLocated(summaryItemPrices));
        // Wait for at least one price element to have text
        longWait.until(d -> {
            List<org.openqa.selenium.WebElement> elements = d.findElements(summaryItemPrices);
            return !elements.isEmpty() && elements.stream().anyMatch(e -> e.getText() != null && !e.getText().isEmpty());
        });
        List<org.openqa.selenium.WebElement> elements = driver.findElements(summaryItemPrices);
        for (org.openqa.selenium.WebElement element : elements) {
            String text = element.getText();
            if (text != null && !text.isEmpty()) {
                prices.add(parseMoney(text));
            }
        }
        return prices;
    }

    public void generatePdf() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(generatePdfButton));
        jsClickSafe(btn);
    }

    private void jsClickSafe(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private double parseMoney(String text) {
        if (text == null) return 0.0;
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\$\\s*([\\d,]+\\.\\d{2})").matcher(text);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1).replace(",", ""));
        }
        return 0.0;
    }
}