package pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import config.TestConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the cart page.
 * Mirrors TypeScript CartPage.ts
 */
public class CartPage {
    private final Page page;

    // Locators
    private final Locator cartItems;
    private final Locator checkoutButton;
    private final Locator continueShoppingButton;
    private final Locator quantityInputs;
    private final Locator itemQuantities;

    public CartPage(Page page) {
        this.page = page;
        this.cartItems = page.locator(".cart_item");
        this.checkoutButton = page.locator(".checkout_button");
        this.continueShoppingButton = page.locator("[data-test=\"continue-shopping\"]");
        this.quantityInputs = cartItems.locator("input");
        this.itemQuantities = page.locator("[data-test=\"item-quantity\"]");
    }

    public void waitForPageLoad() {
        // SPA: wait for cart items to render
        cartItems.first().waitFor(new Locator.WaitForOptions()
                .setTimeout(TestConfig.NAVIGATION_TIMEOUT));
    }

    public List<Double> getCartItemPrices() {
        List<String> texts = cartItems.locator("[data-test=\"inventory-item-price\"]").allTextContents();
        List<Double> prices = new ArrayList<>();
        for (String text : texts) {
            prices.add(parseMoney(text));
        }
        return prices;
    }

    public List<String> getCartItemNames() {
        List<String> names = new ArrayList<>();
        int count = cartItems.count();
        for (int i = 0; i < count; i++) {
            String name = cartItems.nth(i).locator("[data-test=\"inventory-item-name\"]").textContent();
            names.add(name != null ? name : "");
        }
        return names;
    }

    public void removeItem(int index) {
        Locator cartItem = cartItems.nth(index);
        cartItem.locator("button:has-text(\"Remove\")").click();
    }

    public void checkout() {
        checkoutButton.click();
        // Wait for checkout page to load
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED,
                new Page.WaitForLoadStateOptions().setTimeout(TestConfig.NAVIGATION_TIMEOUT));
    }

    public void continueShopping() {
        continueShoppingButton.click();
    }

    public int getCartItemCount() {
        return cartItems.count();
    }

    public void clearCart() {
        int count = cartItems.count();
        for (int i = count - 1; i >= 0; i--) {
            removeItem(i);
        }
    }

    private static double parseMoney(String text) {
        if (text == null) {
            throw new RuntimeException("No amount found for the cart item");
        }
        java.util.regex.Matcher match = java.util.regex.Pattern.compile("\\$\\s*([\\d,]+\\.\\d{2})").matcher(text);
        if (!match.find()) {
            throw new RuntimeException("Could not parse the amount from \"" + text + "\"");
        }
        return Double.parseDouble(match.group(1).replace(",", ""));
    }

    // Getters for locators (needed for assertions)
    public Locator getCartItems() { return cartItems; }
    public Locator getQuantityInputs() { return quantityInputs; }
    public Locator getItemQuantities() { return itemQuantities; }
}
