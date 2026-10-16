package pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import config.TestConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the inventory (products) page.
 * Mirrors TypeScript InventoryPage.ts
 */
public class InventoryPage {
    private final Page page;

    // Locators
    private final Locator products;
    private final Locator sortDropdown;
    private final Locator cartBadge;
    private final Locator cartBadgeCount;
    private final Locator headerTitle;
    private final Locator inventoryLink;
    private final Locator aboutLink;
    private final Locator logoutLink;
    private final Locator productNames;
    private final Locator productDescriptions;
    private final Locator productPrices;
    private final Locator productImages;
    private final Locator addToCartButtons;
    private final Locator socialXLink;
    private final Locator socialFacebookLink;
    private final Locator socialLinkedinLink;
    private final Locator hamburgerMenu;
    private final Locator menuOverlay;
    private final Locator closeButton;

    public InventoryPage(Page page) {
        this.page = page;
        this.products = page.locator(".inventory_item");
        this.sortDropdown = page.locator(".product_sort_container");
        this.cartBadge = page.locator(".shopping_cart_link");
        this.cartBadgeCount = page.locator(".shopping_cart_badge");
        this.headerTitle = page.locator(".title");
        this.inventoryLink = page.locator("[data-test=\"inventory-sidebar-link\"]");
        this.aboutLink = page.locator("[data-test=\"about-sidebar-link\"]");
        this.logoutLink = page.locator("[data-test=\"logout-sidebar-link\"]");
        this.productNames = page.locator(".inventory_item_name");
        this.productDescriptions = page.locator(".inventory_item_desc");
        this.productPrices = page.locator(".inventory_item_price");
        this.productImages = page.locator(".inventory_item img");
        this.addToCartButtons = page.locator(".btn.btn_primary.btn_small.btn_inventory");
        this.socialXLink = page.locator("[data-test=\"social-x\"]");
        this.socialFacebookLink = page.locator("[data-test=\"social-facebook\"]");
        this.socialLinkedinLink = page.locator("[data-test=\"social-linkedin\"]");
        this.hamburgerMenu = page.locator(".bm-burger-button");
        this.menuOverlay = page.locator(".bm-menu-wrap");
        this.closeButton = page.locator(".bm-cross-button");
    }

    public void goTo() {
        Page.NavigateOptions options = new Page.NavigateOptions()
                .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED)
                .setTimeout(TestConfig.NAVIGATION_TIMEOUT);
        page.navigate("inventory.html", options);
    }

    public void clickAbout() {
        aboutLink.click();
    }

    public void waitForPageLoad() {
        headerTitle.waitFor(new Locator.WaitForOptions()
                .setTimeout(TestConfig.DEFAULT_TIMEOUT));
    }

    public int getProductCount() {
        return products.count();
    }

    public Locator getProductElement(int index) {
        return products.nth(index);
    }

    public void addProductToCart(int index) {
        Locator product = products.nth(index);
        product.locator(".btn.btn_primary.btn_small.btn_inventory").click();
    }

    public double getProductPrice(int index) {
        Locator product = products.nth(index);
        String priceText = product.locator(".inventory_item_price").textContent();
        if (priceText == null) {
            throw new RuntimeException("Price not found for product at index " + index);
        }
        return Double.parseDouble(priceText.replace("$", "").trim());
    }

    public String getProductTitle(int index) {
        Locator product = products.nth(index);
        String title = product.locator(".inventory_item_name").textContent();
        return title != null ? title : "";
    }

    public String getProductDescription(int index) {
        Locator product = products.nth(index);
        String desc = product.locator(".inventory_item_desc").textContent();
        return desc != null ? desc : "";
    }

    public int getCartBadgeCount() {
        Locator badge = cartBadgeCount;
        if (!badge.isVisible()) {
            return 0;
        }
        String text = badge.textContent();
        if (text == null || text.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void openCart() {
        cartBadge.click();
        // Wait for cart page content to appear (indicates navigation to cart page)
        page.waitForSelector(".cart_item", new Page.WaitForSelectorOptions()
                .setTimeout(TestConfig.DEFAULT_TIMEOUT));
    }

    public void sortByPriceLowToHigh() {
        sortDropdown.selectOption("Price (low to high)");
    }

    public void sortByPriceHighToLow() {
        sortDropdown.selectOption("Price (high to low)");
    }

    public void sortByAlphaAtoZ() {
        sortDropdown.selectOption("Name (A to Z)");
    }

    public void sortByAlphaZtoA() {
        sortDropdown.selectOption("Name (Z to A)");
    }

    public boolean isProductsVisible() {
        return products.count() > 0;
    }

    public String getTitleText() {
        String title = headerTitle.textContent();
        return title != null ? title : "";
    }

    public List<String> getAllProductNames() {
        List<String> names = new ArrayList<>();
        int count = getProductCount();
        for (int i = 0; i < count; i++) {
            names.add(getProductTitle(i));
        }
        return names;
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
        products.nth(index).locator("[data-test=\"inventory-item-name\"]").click();
    }

    public Locator getProductNameLocator(int index) {
        return products.nth(index).locator("[data-test=\"inventory-item-name\"]");
    }

    // Hamburger menu methods
    public void openHamburgerMenu() {
        // The #react-burger-menu-btn is a transparent overlay button.
        // Use JS click to reach it regardless of z-index/pointer-events.
        page.evaluate("document.querySelector('#react-burger-menu-btn').click()");
        // Wait for menu to open via close button visibility
        closeButton.waitFor(new Locator.WaitForOptions()
                .setTimeout(TestConfig.SLOW_TIMEOUT));
    }

    public void closeHamburgerMenu() {
        // Check if menu is open via close button visibility
        if (closeButton.isVisible()) {
            // Use JS click on the close button
            page.evaluate("document.querySelector('#react-burger-cross-btn').click()");
            // Wait for menu to close - close button should disappear
            page.waitForFunction("() => { "
                + "var btn = document.querySelector('#react-burger-cross-btn');"
                + "return btn === null || !btn.offsetParent; "
                + "}",
                null, new Page.WaitForFunctionOptions().setTimeout(TestConfig.DEFAULT_TIMEOUT));
        }
    }

    public void resetAppState() {
        // Menu should already be open - just click the reset link
        page.locator("[data-test=\"reset-sidebar-link\"]").click();
    }

    public void clickLogout() {
        logoutLink.click();
    }

    /** Click logout after opening the menu (convenience method) */
    public void clickLogoutAndClose() {
        openHamburgerMenu();
        logoutLink.click();
        // Wait for navigation to login page
        page.waitForSelector("[data-test=\"username\"]", new Page.WaitForSelectorOptions()
                .setTimeout(TestConfig.DEFAULT_TIMEOUT));
    }

    public int getMenuItemCount() {
        return page.locator(".bm-item-list a").count();
    }

    public boolean isCloseButtonVisible() {
        Boolean isOpen = (Boolean) page.evaluate("() => { "
            + "var el = document.querySelector('.bm-menu-wrap');"
            + "return el !== null ? el.getAttribute('aria-hidden') === 'false' : false; "
            + "}");
        return isOpen;
    }

    public boolean isInventoryLinkVisible() {
        return isSidebarLinkVisible("[data-test=\"inventory-sidebar-link\"]");
    }

    public boolean isAboutLinkVisible() {
        return isSidebarLinkVisible("[data-test=\"about-sidebar-link\"]");
    }

    public boolean isLogoutLinkVisible() {
        return isSidebarLinkVisible("[data-test=\"logout-sidebar-link\"]");
    }

    private boolean isSidebarLinkVisible(String selector) {
        try {
            Boolean visible = (Boolean) page.evaluate("(sel) => { "
                + "var el = document.querySelector(sel);"
                + "return el !== null && el.offsetParent !== null; "
                + "}", selector);
            return visible != null && visible;
        } catch (Exception e) {
            return false;
        }
    }

    public Locator getMenuItemsLocator() {
        return page.locator(".bm-item-list a");
    }

    // Getters for locators (needed for assertions)
    public Locator getProducts() { return products; }
    public Locator getCartBadgeCountLocator() { return cartBadgeCount; }
    public Locator getHeaderTitle() { return headerTitle; }
    public Locator getProductNames() { return productNames; }
    public Locator getProductDescriptions() { return productDescriptions; }
    public Locator getProductPrices() { return productPrices; }
    public Locator getProductImages() { return productImages; }
    public Locator getAddToCartButtons() { return addToCartButtons; }
    public Locator getInventoryLink() { return inventoryLink; }
    public Locator getAboutLink() { return aboutLink; }
    public Locator getLogoutLink() { return logoutLink; }
    public Locator getHamburgerMenu() { return hamburgerMenu; }
    public Locator getMenuOverlay() { return menuOverlay; }
    public Locator getCloseButton() { return closeButton; }
    public Locator getSocialXLink() { return socialXLink; }
    public Locator getSocialFacebookLink() { return socialFacebookLink; }
    public Locator getSocialLinkedinLink() { return socialLinkedinLink; }
}
