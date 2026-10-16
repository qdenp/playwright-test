import { Page } from "@playwright/test";
import { timeouts } from "../config";

export interface UserCredentials {
  username: string;
  password: string;
}

export interface LoginParams {
  username: string;
  password: string;
  shouldLogin: boolean;
  expectedTitle: string;
  expectedError?: string;
}

export class LoginPage {
  private page: Page;

  // Locators - initialised in the constructor
  usernameInput: ReturnType<Page["locator"]>;
  passwordInput: ReturnType<Page["locator"]>;
  loginButton: ReturnType<Page["locator"]>;
  errorMessage: ReturnType<Page["locator"]>;

  constructor(page: Page) {
    this.page = page;
    this.usernameInput = this.page.locator("#user-name");
    this.passwordInput = this.page.locator("#password");
    this.loginButton = this.page.locator("#login-button");
    this.errorMessage = this.page.locator(".error-message-container");
  }

  async goTo(url: string = ""): Promise<void> {
    // GitHub Pages subpath: use empty string or relative path to avoid baseURL truncation
    // page.goto('/') → https://qdenp.github.io/ (wrong!)
    // page.goto('') → https://qdenp.github.io/playwright-test/ (correct)
    const path = url.startsWith('/') ? url.slice(1) : url;
    await this.page.goto(path, {
      waitUntil: "domcontentloaded",
      timeout: timeouts.NAVIGATION,
    });
  }

  async enterUsername(username: string = "standard_user"): Promise<void> {
    await this.usernameInput.clear();
    await this.usernameInput.fill(username);
  }

  async enterPassword(password: string = "secret_sauce"): Promise<void> {
    await this.passwordInput.clear();
    await this.passwordInput.fill(password);
  }

  async clickLogin(): Promise<void> {
    await this.loginButton.click();
  }

  async login(
    username: string = "standard_user",
    password: string = "secret_sauce",
  ): Promise<void> {
    await this.enterUsername(username);
    await this.enterPassword(password);
    await this.clickLogin();
  }

  async getErrorMessage(): Promise<string | null> {
    return await this.errorMessage.innerText();
  }

  async isErrorMessageVisible(): Promise<boolean> {
    return await this.errorMessage.isVisible();
  }

  async waitForErrorMessage(): Promise<void> {
    await this.errorMessage.waitFor({ state: "visible", timeout: timeouts.DEFAULT });
  }
}
