import { Page } from '@playwright/test';

export class CheckoutPage {
  private page: Page;

  // Locators - инициализируются в constructor
  firstNameInput: ReturnType<Page['locator']>;
  lastNameInput: ReturnType<Page['locator']>;
  zipInput: ReturnType<Page['locator']>;
  continueButton: ReturnType<Page['locator']>;
  finishButton: ReturnType<Page['locator']>;
  cancelButton: ReturnType<Page['locator']>;
  errorMessage: ReturnType<Page['locator']>;
  thankYouMessage: ReturnType<Page['locator']>;
  completeTitle: ReturnType<Page['locator']>;
  backToProducts: ReturnType<Page['locator']>;

  constructor(page: Page) {
    this.page = page;
    this.firstNameInput = this.page.locator('[data-test="firstName"]');
    this.lastNameInput = this.page.locator('[data-test="lastName"]');
    this.zipInput = this.page.locator('[data-test="postalCode"]');
    this.continueButton = this.page.getByRole('button', { name: 'Continue' });
    this.finishButton = this.page.getByRole('button', { name: 'Finish' });
    this.cancelButton = this.page.locator('.button.btn_secondary.btn_small.btn_footer');
    this.errorMessage = this.page.locator('.error-message-container');
    this.thankYouMessage = this.page.locator('.summary_text, .summary_label');
    this.completeTitle = this.page.locator('.complete-header');
    this.backToProducts = this.page.locator('.btn.btn_action.btn_small');
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

  async getThankYouMessage(): Promise<string | null> {
    return await this.thankYouMessage.textContent();
  }

  async getCompleteText(): Promise<string | null> {
    return await this.completeTitle.textContent();
  }

  async waitForPageLoad(): Promise<void> {
    await this.firstNameInput.waitFor({ state: 'visible', timeout: 10000 });
  }

  async submitCheckoutForm(firstName: string, lastName: string, zipCode: string): Promise<void> {
    await this.fillFirstName(firstName);
    await this.fillLastName(lastName);
    await this.fillZipCode(zipCode);
    await this.clickContinue();
  }
}
