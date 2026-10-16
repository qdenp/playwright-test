import { Download, Page } from "@playwright/test";
import { timeouts } from "../config";

export class CheckoutPage {
  private page: Page;

  // Locators - initialised in the constructor
  firstNameInput: ReturnType<Page["locator"]>;
  lastNameInput: ReturnType<Page["locator"]>;
  zipInput: ReturnType<Page["locator"]>;
  continueButton: ReturnType<Page["locator"]>;
  finishButton: ReturnType<Page["locator"]>;
  cancelButton: ReturnType<Page["locator"]>;
  errorMessage: ReturnType<Page["locator"]>;
  completeTitle: ReturnType<Page["locator"]>;
  backToProducts: ReturnType<Page["locator"]>;
  generatePdfButton: ReturnType<Page["locator"]>;
  itemsTotalLabel: ReturnType<Page["locator"]>;
  taxLabel: ReturnType<Page["locator"]>;
  totalLabel: ReturnType<Page["locator"]>;
  summaryItemPrices: ReturnType<Page["locator"]>;

  constructor(page: Page) {
    this.page = page;
    this.firstNameInput = this.page.locator('[data-test="firstName"]');
    this.lastNameInput = this.page.locator('[data-test="lastName"]');
    this.zipInput = this.page.locator('[data-test="postalCode"]');
    this.continueButton = this.page.getByRole("button", { name: "Continue" });
    this.finishButton = this.page.getByRole("button", { name: "Finish" });
    this.cancelButton = this.page.locator(
      ".button.btn_secondary.btn_small.btn_footer",
    );
    this.errorMessage = this.page.locator(".error-message-container");
    this.completeTitle = this.page.locator(".complete-header");
    this.backToProducts = this.page.locator(".btn.btn_action.btn_small");
    this.generatePdfButton = this.page.getByRole("button", { name: "Generate PDF" });
    this.itemsTotalLabel = this.page.locator(".summary_subtotal_label");
    this.taxLabel = this.page.locator(".summary_tax_label");
    this.totalLabel = this.page.locator(".summary_total_label");
    this.summaryItemPrices = this.page.locator(".cart_item .inventory_item_price");
  }

  async fillFirstName(firstName: string): Promise<void> {
    await this.firstNameInput.fill(firstName);
  }

  async fillLastName(lastName: string): Promise<void> {
    await this.lastNameInput.fill(lastName);
  }

  async fillZipCode(zipCode: string): Promise<void> {
    await this.zipInput.fill(zipCode);
  }

  async clickContinue(): Promise<void> {
    await this.continueButton.click();
  }

  async clickFinish(): Promise<void> {
    await this.finishButton.click();
  }

  async clickCancel(): Promise<void> {
    await this.cancelButton.click();
  }

  async clickBackToProducts(): Promise<void> {
    await this.backToProducts.click();
  }

  async getErrorMessage(): Promise<string | null> {
    return await this.errorMessage.innerText();
  }

  async isErrorMessageVisible(): Promise<boolean> {
    return await this.errorMessage.isVisible();
  }

  async waitForPageLoad(): Promise<void> {
    await this.firstNameInput.waitFor({ state: "visible", timeout: timeouts.DEFAULT });
  }

  async submitCheckoutForm(
    firstName: string,
    lastName: string,
    zipCode: string,
  ): Promise<void> {
    await this.fillFirstName(firstName);
    await this.fillLastName(lastName);
    await this.fillZipCode(zipCode);
    await this.clickContinue();
  }

  async fillInfo(
    firstName: string,
    lastName: string,
    zipCode: string,
  ): Promise<void> {
    await this.fillFirstName(firstName);
    await this.fillLastName(lastName);
    await this.fillZipCode(zipCode);
  }

  get completeText(): ReturnType<Page["locator"]> {
    return this.completeTitle;
  }

  /**
   * Sum of all items in the checkout summary ("Item total: $55.97").
   */
  async getItemsTotal(): Promise<number> {
    return CheckoutPage.parseMoney(await this.itemsTotalLabel.textContent());
  }

  /** Tax from the checkout summary ("Tax: $4.48"). */
  async getTax(): Promise<number> {
    return CheckoutPage.parseMoney(await this.taxLabel.textContent());
  }

  /** Total order amount ("Total: $60.45"). */
  async getTotal(): Promise<number> {
    return CheckoutPage.parseMoney(await this.totalLabel.textContent());
  }

  /** Prices of the individual items in the checkout summary, in order. */
  async getSummaryItemPrices(): Promise<number[]> {
    const texts = await this.summaryItemPrices.allTextContents();
    return texts.map((text) => CheckoutPage.parseMoney(text));
  }

  private static parseMoney(text: string | null): number {
    if (text === null) {
      throw new Error("No amount found in the checkout summary");
    }
    const match = text.match(/\$\s*([\d,]+\.\d{2})/);
    if (!match) {
      throw new Error(`Could not parse the amount from "${text}"`);
    }
    return parseFloat(match[1].replace(/,/g, ""));
  }

  /** Export the order as PDF: click the button and wait for the download */
  async generatePdf(): Promise<Download> {
    const [download] = await Promise.all([
      this.page.waitForEvent("download"),
      this.generatePdfButton.click(),
    ]);
    return download;
  }
}
