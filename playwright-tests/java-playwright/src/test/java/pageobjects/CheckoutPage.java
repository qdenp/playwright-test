package pageobjects;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import config.TestConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the checkout page.
 * Mirrors TypeScript CheckoutPage.ts
 */
public class CheckoutPage {
    private final Page page;

    // Locators
    private final Locator firstNameInput;
    private final Locator lastNameInput;
    private final Locator zipInput;
    private final Locator continueButton;
    private final Locator finishButton;
    private final Locator cancelButton;
    private final Locator errorMessage;
    private final Locator completeTitle;
    private final Locator backToProducts;
    private final Locator generatePdfButton;
    private final Locator itemsTotalLabel;
    private final Locator taxLabel;
    private final Locator totalLabel;
    private final Locator summaryItemPrices;

    public CheckoutPage(Page page) {
        this.page = page;
        this.firstNameInput = page.locator("[data-test=\"firstName\"]");
        this.lastNameInput = page.locator("[data-test=\"lastName\"]");
        this.zipInput = page.locator("[data-test=\"postalCode\"]");
        this.continueButton = page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
        this.finishButton = page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Finish"));
        this.cancelButton = page.locator(".button.btn_secondary.btn_small.btn_footer");
        this.errorMessage = page.locator(".error-message-container");
        this.completeTitle = page.locator(".complete-header");
        this.backToProducts = page.locator(".btn.btn_action.btn_small");
        this.generatePdfButton = page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Generate PDF"));
        this.itemsTotalLabel = page.locator(".summary_subtotal_label");
        this.taxLabel = page.locator(".summary_tax_label");
        this.totalLabel = page.locator(".summary_total_label");
        this.summaryItemPrices = page.locator(".cart_item .inventory_item_price");
    }

    public void fillFirstName(String firstName) {
        firstNameInput.fill(firstName);
    }

    public void fillLastName(String lastName) {
        lastNameInput.fill(lastName);
    }

    public void fillZipCode(String zipCode) {
        zipInput.fill(zipCode);
    }

    public void clickContinue() {
        continueButton.click();
    }

    public void clickFinish() {
        finishButton.click();
    }

    public void clickCancel() {
        cancelButton.click();
    }

    public void clickBackToProducts() {
        backToProducts.click();
    }

    public String getErrorMessage() {
        return errorMessage.innerText();
    }

    public boolean isErrorMessageVisible() {
        return errorMessage.isVisible();
    }

    public void waitForPageLoad() {
        firstNameInput.waitFor(new Locator.WaitForOptions()
                .setTimeout(TestConfig.DEFAULT_TIMEOUT));
    }

    public void submitCheckoutForm(String firstName, String lastName, String zipCode) {
        fillFirstName(firstName);
        fillLastName(lastName);
        fillZipCode(zipCode);
        clickContinue();
    }

    public void fillInfo(String firstName, String lastName, String zipCode) {
        fillFirstName(firstName);
        fillLastName(lastName);
        fillZipCode(zipCode);
    }

    public Locator getCompleteTitle() {
        return completeTitle;
    }

    /**
     * Sum of all items in the checkout summary ("Item total: $55.97").
     */
    public double getItemsTotal() {
        return parseMoney(itemsTotalLabel.textContent());
    }

    /** Tax from the checkout summary ("Tax: $4.48"). */
    public double getTax() {
        return parseMoney(taxLabel.textContent());
    }

    /** Total order amount ("Total: $60.45"). */
    public double getTotal() {
        return parseMoney(totalLabel.textContent());
    }

    /** Prices of the individual items in the checkout summary, in order. */
    public List<Double> getSummaryItemPrices() {
        List<String> texts = summaryItemPrices.allTextContents();
        List<Double> prices = new ArrayList<>();
        for (String text : texts) {
            prices.add(parseMoney(text));
        }
        return prices;
    }

    private static double parseMoney(String text) {
        if (text == null) {
            throw new RuntimeException("No amount found in the checkout summary");
        }
        java.util.regex.Matcher match = java.util.regex.Pattern.compile("\\$\\s*([\\d,]+\\.\\d{2})").matcher(text);
        if (!match.find()) {
            throw new RuntimeException("Could not parse the amount from \"" + text + "\"");
        }
        return Double.parseDouble(match.group(1).replace(",", ""));
    }

    /** Export the order as PDF: click the button and wait for the download */
    public Download generatePdf() {
        return page.waitForDownload(new Page.WaitForDownloadOptions(), () -> {
            generatePdfButton.click();
        });
    }

    // Getters for locators (needed for assertions)
    public Locator getFirstNameInput() { return firstNameInput; }
    public Locator getContinueButton() { return continueButton; }
    public Locator getFinishButton() { return finishButton; }
    public Locator getErrorMessageLocator() { return errorMessage; }
}
