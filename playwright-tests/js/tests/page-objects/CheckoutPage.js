/**
 * Page Object для оформления заказа (Checkout)
 */
import { timeouts } from "../config.js";

export class CheckoutPage {
  constructor(page) {
    this.page = page;
    this.firstNameInput = page.locator("#first-name");
    this.lastNameInput = page.locator("#last-name");
    this.zipCodeInput = page.locator("#postal-code");
    this.continueButton = page.locator("#continue");
    this.errorMessage = page.locator(".error-message-container.error");
    this.orderSummary = page.locator(".summary_subtotal_label");
    this.finishButton = page.locator("#finish");
    this.thankYouMessage = page.locator("text=Thank you for your order!");
    this.completeText = page.locator(".complete-text");
    this.backToHomeButton = page.locator(".btn_action");
    this.itemsInOrder = page.locator(".cart_item");
    this.itemPrices = page.locator(".inventory_item_price");
    this.itemTotals = page.locator(".summary_total_label");
  }

  async waitForPageLoad() {
    await this.firstNameInput.waitFor({ state: "visible", timeout: 10000 });
  }

  async fillInfo(firstName, lastName, zipCode) {
    await this.firstNameInput.fill(firstName);
    await this.lastNameInput.fill(lastName);
    await this.zipCodeInput.fill(zipCode);
  }

  async clickContinue() {
    await this.continueButton.click();
  }

  async clickFinish() {
    await this.finishButton.click();
  }

  async getErrorMessage() {
    return await this.errorMessage.textContent();
  }

  async isErrorMessageVisible() {
    return await this.errorMessage.isVisible();
  }

  async getThankYouMessage() {
    return await this.thankYouMessage.textContent();
  }

  async isCompleteHeaderVisible() {
    await this.thankYouMessage.waitFor({ state: "attached", timeout: timeouts.DEFAULT });
    return await this.thankYouMessage.isVisible();
  }

  async getCompleteText() {
    return await this.completeText.textContent();
  }

  async clickBackToHome() {
    await this.backToHomeButton.click();
  }

  async getOrderSummary() {
    const summary = {};
    summary.items = await this.itemsInOrder.allTextContents();
    summary.prices = await this.itemPrices.allTextContents();
    return summary;
  }

  async isContinueButtonDisabled() {
    return await this.continueButton.isDisabled();
  }

  async getFirstNameValue() {
    return await this.firstNameInput.inputValue();
  }

  async getLastNameValue() {
    return await this.lastNameInput.inputValue();
  }

  async getZipCodeValue() {
    return await this.zipCodeInput.inputValue();
  }
}
