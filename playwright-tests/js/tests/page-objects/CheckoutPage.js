/**
 * Page Object для оформления заказа (Checkout)
 */
import { timeouts } from "../config.js";

export class CheckoutPage {
  constructor(page) {
    this.page = page;
    this.firstNameInput = page.locator('[data-test="firstName"]');
    this.lastNameInput = page.locator('[data-test="lastName"]');
    this.zipCodeInput = page.locator('[data-test="postalCode"]');
    this.zipInput = this.zipCodeInput;
    this.continueButton = page.getByRole("button", { name: "Continue" });
    this.finishButton = page.getByRole("button", { name: "Finish" });
    this.cancelButton = page.locator(".button.btn_secondary.btn_small.btn_footer");
    this.errorMessage = page.locator(".error-message-container");
    this.completeTitle = page.locator(".complete-header");
    this.backToProducts = page.locator(".btn.btn_action.btn_small");
    this.generatePdfButton = page.getByRole("button", { name: "Generate PDF" });
    this.itemsTotalLabel = page.locator(".summary_subtotal_label");
    this.taxLabel = page.locator(".summary_tax_label");
    this.totalLabel = page.locator(".summary_total_label");
    this.summaryItemPrices = page.locator(".cart_item .inventory_item_price");
  }

  async fillFirstName(firstName) {
    await this.firstNameInput.fill(firstName);
  }

  async fillLastName(lastName) {
    await this.lastNameInput.fill(lastName);
  }

  async fillZipCode(zipCode) {
    await this.zipInput.fill(zipCode);
  }

  async clickContinue() {
    await this.continueButton.click();
  }

  async clickFinish() {
    await this.finishButton.click();
  }

  async clickCancel() {
    await this.cancelButton.click();
  }

  async clickBackToProducts() {
    await this.backToProducts.click();
  }

  getErrorMessage() {
    return this.errorMessage.innerText();
  }

  isErrorMessageVisible() {
    return this.errorMessage.isVisible();
  }

  async waitForPageLoad() {
    await this.firstNameInput.waitFor({ state: "visible", timeout: timeouts.DEFAULT });
  }

  async submitCheckoutForm(firstName, lastName, zipCode) {
    await this.fillFirstName(firstName);
    await this.fillLastName(lastName);
    await this.fillZipCode(zipCode);
    await this.clickContinue();
  }

  async fillInfo(firstName, lastName, zipCode) {
    await this.fillFirstName(firstName);
    await this.fillLastName(lastName);
    await this.fillZipCode(zipCode);
  }

  /** Экспорт заказа в PDF: клик по кнопке и ожидание скачивания */
  async generatePdf() {
    const [download] = await Promise.all([
      this.page.waitForEvent("download"),
      this.generatePdfButton.click(),
    ]);
    return download;
  }

  /**
   * Сумма всех товаров из сводки чекаута ("Item total: $55.97").
   */
  async getItemsTotal() {
    return CheckoutPage.parseMoney(await this.itemsTotalLabel.textContent());
  }

  /** Налог из сводки чекаута ("Tax: $4.48"). */
  async getTax() {
    return CheckoutPage.parseMoney(await this.taxLabel.textContent());
  }

  /** Итоговая сумма заказа ("Total: $60.45"). */
  async getTotal() {
    return CheckoutPage.parseMoney(await this.totalLabel.textContent());
  }

  /** Цены отдельных товаров из сводки чекаута, по порядку. */
  async getSummaryItemPrices() {
    const texts = await this.summaryItemPrices.allTextContents();
    return texts.map((text) => CheckoutPage.parseMoney(text));
  }

  static parseMoney(text) {
    if (text === null) {
      throw new Error("Не найдена сумма в сводке чекаута");
    }
    const match = text.match(/\$\s*([\d,]+\.\d{2})/);
    if (!match) {
      throw new Error(`Не удалось разобрать сумму из "${text}"`);
    }
    return parseFloat(match[1].replace(/,/g, ""));
  }
}
