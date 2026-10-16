/**
 * Page Object for the login page
 */
import { timeouts } from "../config";

export class LoginPage {
  constructor(page) {
    this.page = page;
    this.usernameInput = page.locator("#user-name");
    this.passwordInput = page.locator("#password");
    this.loginButton = page.locator("#login-button");
    this.errorMessage = page.locator(".error-message-container");
  }

  async goTo(url = "") {
    // GitHub Pages subpath: use empty string or relative path to avoid baseURL truncation
    // page.goto('/') → https://qdenp.github.io/ (wrong!)
    // page.goto('') → https://qdenp.github.io/playwright-test/ (correct)
    const path = url.startsWith('/') ? url.slice(1) : url;
    await this.page.goto(path, {
      waitUntil: "domcontentloaded",
      timeout: timeouts.NAVIGATION,
    });
  }

  async enterUsername(username = "standard_user") {
    await this.usernameInput.clear();
    await this.usernameInput.fill(username);
  }

  async enterPassword(password = "secret_sauce") {
    await this.passwordInput.clear();
    await this.passwordInput.fill(password);
  }

  async clickLogin() {
    await this.loginButton.click();
  }

  async login(
    username = "standard_user",
    password = "secret_sauce",
  ) {
    await this.enterUsername(username);
    await this.enterPassword(password);
    await this.clickLogin();
  }

  async getErrorMessage() {
    return await this.errorMessage.innerText();
  }

  async isErrorMessageVisible() {
    return await this.errorMessage.isVisible();
  }

  async waitForErrorMessage() {
    await this.errorMessage.waitFor({ state: "visible", timeout: timeouts.DEFAULT });
  }
}
