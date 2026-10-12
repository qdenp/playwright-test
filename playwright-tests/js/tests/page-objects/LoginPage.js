/**
 * Page Object for the login page
 */
import { config, timeouts } from "../config.js";

export class LoginPage {
  constructor(page) {
    this.page = page;
    this.usernameInput = page.locator("#user-name");
    this.passwordInput = page.locator("#password");
    this.loginButton = page.locator("#login-button");
    this.errorMessage = page.locator(".error-message-container");
    this.hamburgerMenu = page.locator(".bm-burger-button");
    this.closeButton = page.locator(".bm-cross-button");
    this.menuOverlay = page.locator(".bm-menu-wrap");
  }

  async goTo() {
    await this.page.goto(config.baseUrl, {
      waitUntil: "domcontentloaded",
      timeout: timeouts.NAVIGATION,
    });
  }

  async enterUsername(username) {
    await this.usernameInput.fill(username);
  }

  async enterPassword(password) {
    await this.passwordInput.fill(password);
  }

  async clickLogin() {
    await this.loginButton.click();
  }

  async login(username = null, password = null) {
    /** Login with the config credentials by default.
     *  Use `??` rather than `||`: an empty string is valid input for the
     *  "empty username" case, whereas `||` would substitute the default. */
    const user = username ?? config.username;
    const pwd = password ?? config.password;
    await this.enterUsername(user);
    await this.enterPassword(pwd);
    await this.clickLogin();
  }

  async getErrorMessage() {
    return await this.errorMessage.innerText();
  }

  async isErrorMessageVisible() {
    return await this.errorMessage.isVisible();
  }

  async waitForErrorMessage(timeout = timeouts.DEFAULT) {
    await this.errorMessage.waitFor({ state: "visible", timeout });
  }

  async openHamburgerMenu() {
    await this.hamburgerMenu.click();
  }

  async closeHamburgerMenu() {
    await this.closeButton.click();
  }

  async isHamburgerMenuOpen() {
    return await this.closeButton.isVisible();
  }

  async getMenuItemItems() {
    const menuItems = await this.page.locator(".bm-item-list a").allTextContents();
    return menuItems.map((item) => item.trim());
  }

  async clickBackToProducts() {
    await this.page.getByRole("link", { name: "Back to Products" }).click();
  }
}
