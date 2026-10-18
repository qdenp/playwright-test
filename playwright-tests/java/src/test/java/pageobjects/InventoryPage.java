package pageobjects;

import config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

/**
 * Page Object for the inventory page.
 */
public class InventoryPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public final By products = By.cssSelector(".inventory_item");
    public final By sortDropdown = By.cssSelector(".product_sort_container");
    public final By cartBadge = By.cssSelector(".shopping_cart_link");
    // Badge is inside .shopping_cart_link, not a standalone element
    public final By cartBadgeCount = By.cssSelector(".shopping_cart_link .shopping_cart_badge");
    public final By headerTitle = By.cssSelector(".title");
    public final By inventoryLink = By.cssSelector("[data-test='inventory-sidebar-link']");
    public final By aboutLink = By.cssSelector("[data-test='about-sidebar-link']");
    public final By logoutLink = By.cssSelector("[data-test='logout-sidebar-link']");
    public final By closeButton = By.id("react-burger-cross-btn");
    public final By productNames = By.cssSelector(".inventory_item_name");
    public final By productDescriptions = By.cssSelector(".inventory_item_desc");
    public final By productPrices = By.cssSelector(".inventory_item_price");
    public final By productImages = By.cssSelector(".inventory_item img");
    public final By addToCartButtons = By.cssSelector(".btn.btn_primary.btn_small.btn_inventory");
    public final By socialXLink = By.cssSelector("[data-test='social-x']");
    public final By socialFacebookLink = By.cssSelector("[data-test='social-facebook']");
    public final By socialLinkedinLink = By.cssSelector("[data-test='social-linkedin']");

    private final By resetSidebarLink = By.cssSelector("[data-test='reset-sidebar-link']");
    private final By menuItems = By.cssSelector(".bm-item-list a");
    private final By inventoryItemName = By.cssSelector("[data-test='inventory-item-name']");
    private final By backToProductsButton = By.cssSelector("[data-test='back-to-products']");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.DEFAULT_TIMEOUT));
    }

    public void goTo() {
        driver.get(TestConfig.BASE_URL + "inventory.html");
        waitForPageLoad();
    }

    public void waitForPageLoad() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.NAVIGATION_TIMEOUT));
        longWait.until(ExpectedConditions.or(
            ExpectedConditions.visibilityOfElementLocated(headerTitle),
            ExpectedConditions.presenceOfElementLocated(products)
        ));
        longWait.until(ExpectedConditions.visibilityOfElementLocated(productNames));
    }

    public int getProductCount() {
        return driver.findElements(products).size();
    }

    public WebElement getProductElement(int index) {
        return driver.findElements(products).get(index);
    }

    public void addProductToCart(int index) {
        WebElement product = getProductElement(index);
        WebElement addButton = product.findElement(By.cssSelector(".btn.btn_primary.btn_small.btn_inventory"));
        // Use JavaScript click for React SPA (more reliable than Selenium click)
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", addButton);
        // React SPA needs time to update the cart badge
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.DEFAULT_TIMEOUT));
        longWait.until(d -> {
            try {
                String text = (String) ((org.openqa.selenium.JavascriptExecutor) d)
                    .executeScript("var badge = document.querySelector('.shopping_cart_badge'); return badge ? badge.textContent : null;");
                return text != null && !text.trim().isEmpty();
            } catch (Exception e) {
                return false;
            }
        });
    }

    public void addProductByName(String productName) {
        String xpath = String.format("//div[contains(@class,'inventory_item') and .//text()='%s']", productName);
        WebElement product = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        product.findElement(By.cssSelector(".btn.btn_primary.btn_small.btn_inventory")).click();
    }

    public double getProductPrice(int index) {
        WebElement product = getProductElement(index);
        String priceText = product.findElement(productPrices).getText();
        if (priceText == null || priceText.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(priceText.replace("$", "").replace(",", ""));
    }

    public String getProductDescription(int index) {
        WebElement product = getProductElement(index);
        return product.findElement(productDescriptions).getText();
    }

    public String getProductTitle(int index) {
        WebElement product = getProductElement(index);
        return product.findElement(productNames).getText();
    }

    public int getCartBadgeCount() {
        try {
            String text = (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "var badge = document.querySelector('.shopping_cart_badge');"
                + "if (!badge) return null;"
                + "var cs = window.getComputedStyle(badge);"
                + "if (cs.visibility === 'hidden' || cs.display === 'none' || parseFloat(cs.opacity) === 0) return null;"
                + "return badge.textContent || null;");
            return text != null && !text.isEmpty() ? Integer.parseInt(text) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public void waitForCartBadgeCount(int expectedCount) {
        // Use JavaScript to get badge text directly - React SPA renders with delay
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.SLOW_TIMEOUT));
        longWait.until(d -> {
            try {
                // Get badge text via JavaScript (more reliable than Selenium for SPA)
                String text = (String) ((org.openqa.selenium.JavascriptExecutor) d)
                    .executeScript("var badge = document.querySelector('.shopping_cart_badge'); return badge ? badge.textContent : null;");
                return text != null && text.trim().equals(String.valueOf(expectedCount));
            } catch (Exception e) {
                return false;
            }
        });
    }

    public void openCart() {
        // Use JavaScript click for React SPA
        WebElement cartLink = driver.findElement(cartBadge);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", cartLink);
        // Wait for SPA navigation - React updates URL with delay
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.NAVIGATION_TIMEOUT));
        longWait.until(d -> {
            String url = d.getCurrentUrl();
            return url.contains("cart.html") || url.contains("cart");
        });
    }

    public void sortByPriceLowToHigh() {
        new Select(driver.findElement(sortDropdown)).selectByVisibleText("Price (low to high)");
    }

    public void sortByPriceHighToLow() {
        new Select(driver.findElement(sortDropdown)).selectByVisibleText("Price (high to low)");
    }

    public void sortByNameAToZ() {
        new Select(driver.findElement(sortDropdown)).selectByVisibleText("Name (A to Z)");
    }

    public void sortByNameZToA() {
        new Select(driver.findElement(sortDropdown)).selectByVisibleText("Name (Z to A)");
    }

    public boolean isProductsVisible() {
        try {
            return driver.findElements(products).get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getTitleText() {
        return driver.findElement(headerTitle).getText();
    }

    public void openHamburgerMenu() {
        WebElement hamburger = driver.findElement(By.id("react-burger-menu-btn"));
        // The #react-burger-menu-btn is a full-screen transparent overlay
        // (z-index:1, width:100%, height:100%) sitting above page content.
        // Selenium's coordinate-based Actions.click() hits the content on top;
        // a JS DOM click reaches the button regardless of z-index.
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "arguments[0].click();", hamburger);
        // Chrome 155's isDisplayed() returns false for the overlay-style close
        // button even when it is visible. Wait for the menu via aria-hidden on
        // bm-menu-wrap, which the React SPA toggles when the menu opens/closes.
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
            // Check if menu is open via aria-hidden (isDisplayed fails in Chrome 155)
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

    public void resetAppState() {
        openHamburgerMenu();
        // Check menu items via JS (isDisplayed fails for overlay elements in Chrome 155)
        wait.until(d -> {
            try {
                Boolean result = (Boolean) ((org.openqa.selenium.JavascriptExecutor) d).executeScript(
                    "var el = document.querySelector('.bm-item-list a');"
                    + "return el !== null && el.offsetParent !== null;");
                return result != null && result;
            } catch (Exception e) {
                return false;
            }
        });
        WebElement resetLink = driver.findElement(resetSidebarLink);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "arguments[0].click();", resetLink);
    }

    public List<String> getAllProductNames() {
        List<String> names = new ArrayList<>();
        int count = getProductCount();
        for (int i = 0; i < count; i++) {
            names.add(getProductTitle(i));
        }
        return names;
    }

    public void clickAbout() {
        WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(aboutLink));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "arguments[0].click();", link);
    }

    public List<Double> getAllProductPrices() {
        List<Double> prices = new ArrayList<>();
        int count = getProductCount();
        for (int i = 0; i < count; i++) {
            prices.add(getProductPrice(i));
        }
        return prices;
    }

    public void clickProductName(int index) {
        WebElement product = getProductElement(index);
        WebElement nameLink = product.findElement(By.cssSelector("[data-test='inventory-item-name']"));
        // Use JS click: the full-screen hamburger overlay (z-index:1) can
        // intercept Selenium's coordinate-based click on this page.
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "arguments[0].click();", nameLink);
    }

    public WebElement getProductNameLocator(int index) {
        return getProductElement(index).findElement(By.cssSelector("[data-test='inventory-item-name']"));
    }

    public void clickLogout() {
        // The logout link is inside the hamburger menu - open it first.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='primary-header']")));
        openHamburgerMenu();
        // Wait for menu items to be present (isDisplayed may fail on overlay
        // elements in Chrome 155, so use a JS offsetParent check on the list).
        wait.until(d -> {
            try {
                Boolean result = (Boolean) ((org.openqa.selenium.JavascriptExecutor) d).executeScript(
                    "var el = document.querySelector('.bm-item-list a');"
                    + "return el !== null && el.offsetParent !== null;");
                return result != null && result;
            } catch (Exception e) {
                return false;
            }
        });
        WebElement logoutEl = driver.findElement(logoutLink);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "arguments[0].click();", logoutEl);
        // The SPA redirects to the login page after logout; wait for it.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
    }

    public List<WebElement> getMenuItemsLocator() {
        return driver.findElements(menuItems);
    }

    public int getMenuItemCount() {
        return getMenuItemsLocator().size();
    }

    public boolean isInventoryLinkVisible() {
        return isSidebarLinkVisible(inventoryLink);
    }

    public boolean isAboutLinkVisible() {
        return isSidebarLinkVisible(aboutLink);
    }

    public boolean isLogoutLinkVisible() {
        return isSidebarLinkVisible(logoutLink);
    }

    private boolean isSidebarLinkVisible(By locator) {
        try {
            Boolean visible = (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "var el = arguments[0];"
                + "return el !== null && el.offsetParent !== null"
                + "  && window.getComputedStyle(el).visibility !== 'hidden'"
                + "  && window.getComputedStyle(el).display !== 'none';",
                driver.findElement(locator));
            return visible != null && visible;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFirstProductVisible() {
        try {
            return driver.findElements(productNames).get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCloseButtonVisible() {
        try {
            String hidden = (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "var el = document.querySelector('.bm-menu-wrap');"
                + "return el !== null ? el.getAttribute('aria-hidden') : null;");
            return "false".equals(hidden);
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForCartBadgeAbsent() {
        wait.until(d -> {
            try {
                Boolean absent = (Boolean) ((org.openqa.selenium.JavascriptExecutor) d).executeScript(
                    "var badge = document.querySelector('.shopping_cart_badge');"
                    + "if (!badge) return true;"
                    + "var cs = window.getComputedStyle(badge);"
                    + "return cs.visibility === 'hidden' || cs.display === 'none'"
                    + "  || parseFloat(cs.opacity) === 0 || !badge.textContent.trim();");
                return absent != null && absent;
            } catch (Exception e) {
                return false;
            }
        });
    }

    public void continueShopping() {
        WebElement link = wait.until(ExpectedConditions.presenceOfElementLocated(inventoryLink));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "arguments[0].click();", link);
    }
}