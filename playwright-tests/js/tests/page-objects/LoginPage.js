/**
 * Page Object для страницы логина
 */
import { config } from "../config.js";

export class LoginPage {
  constructor(page) {
    this.page = page;
    this.usernameInput = page.locator("#user-name");
    this.passwordInput = page.locator("#password");
    this.loginButton = page.locator("#login-button");
    this.errorMessage = page.locator(".error-message-container");
    this.hamburgerMenu = page.locator(".bm-burger-button");
    this.closeMenu = page.locator(".bm-cross-button");
    this.menuOverlay = page.locator(".bm-menu");
  }

  async goTo() {
    await this.page.goto(config.baseUrl, {
      waitUntil: "domcontentloaded",
      timeout: 15000,
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
    /** Логин с credentials из config по умолчанию */
    const user = username || config.username;
    const pwd = password || config.password;
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

  async waitForErrorMessage(timeout = 10000) {
    await this.errorMessage.waitFor({ state: "visible", timeout });
  }

  async openHamburgerMenu() {
    await this.hamburgerMenu.click();
  }

  async closeHamburgerMenu() {
    await this.closeMenu.click();
  }

  async isHamburgerMenuOpen() {
    return await this.closeMenu.isVisible();
  }

  async clickBackToProducts() {
    await this.page.getByRole("link", { name: "Back to Products" }).click();
  }

  async clickLogout() {
    await this.page.getByRole("menuitem", { name: "Logout" }).click();
  }

  getMenuItemsLocator() {
    return this.page.locator(".bm-item-list a");
  }

  async getMenuItemCount() {
    return await this.getMenuItemsLocator().count();
  }

  async isInventoryLinkVisible() {
    return await this.page.locator('[data-test="inventory-sidebar-link"]').isVisible();
  }

  async isAboutLinkVisible() {
    return await this.page.locator('[data-test="about-sidebar-link"]').isVisible();
  }

  async isLogoutLinkVisible() {
    return await this.page.locator('[data-test="logout-sidebar-link"]').isVisible();
  }
}
