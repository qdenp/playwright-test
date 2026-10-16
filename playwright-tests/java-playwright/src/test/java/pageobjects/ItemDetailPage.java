package pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object for the item detail page.
 * Mirrors TypeScript ItemDetailPage.ts
 */
public class ItemDetailPage {
    private final Page page;

    // Locators
    private final Locator detailDescription;
    private final Locator backToProducts;

    public ItemDetailPage(Page page) {
        this.page = page;
        this.detailDescription = page.locator(".inventory_details_desc");
        this.backToProducts = page.locator("[data-test=\"back-to-products\"]");
    }

    public void waitForPageLoad() {
        detailDescription.waitFor(new Locator.WaitForOptions()
                .setTimeout(5000));
    }

    public String getDetailDescription() {
        String desc = detailDescription.textContent();
        return desc != null ? desc : "";
    }

    public void clickBackToProducts() {
        backToProducts.click();
    }

    // Getters for locators
    public Locator getDetailDescriptionLocator() { return detailDescription; }
    public Locator getBackToProductsLocator() { return backToProducts; }
}
