import { Page } from "@playwright/test";

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

  // Locators - инициализируются в constructor
  usernameInput: ReturnType<Page["locator"]>;
  passwordInput: ReturnType<Page["locator"]>;
  loginButton: ReturnType<Page["locator"]>;
  errorMessage: ReturnType<Page["locator"]>;
  hamburgerMenu: ReturnType<Page["locator"]>;
  menuOverlay: ReturnType<Page["locator"]>;
  closeButton: ReturnType<Page["locator"]>;
  logoutMenuItem: ReturnType<Page["locator"]>;
  aboutMenuItem: ReturnType<Page["locator"]>;

  constructor(page: Page) {
    this.page = page;
    this.usernameInput = this.page.locator("#user-name");
    this.passwordInput = this.page.locator("#password");
    this.loginButton = this.page.locator("#login-button");
    this.errorMessage = this.page.locator(".error-message-container");
    this.hamburgerMenu = this.page.locator(".bm-burger-button");
    this.menuOverlay = this.page.locator(".bm-menu-wrap");
    this.closeButton = this.page.locator(".bm-cross-button");
    this.logoutMenuItem = this.page.locator(
      '.bm-item-list a:has-text("Logout")',
    );
    this.aboutMenuItem = this.page.locator('.bm-item-list a:has-text("About")');
  }

  async goTo(url: string = "/"): Promise<void> {
    await this.page.goto(url);
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

  async openHamburgerMenu(): Promise<void> {
    await this.hamburgerMenu.click();
  }

  async closeHamburgerMenu(): Promise<void> {
    await this.closeButton.click();
  }

  async clickLogout(): Promise<void> {
    await this.page.locator('[data-test="logout-sidebar-link"]').click();
  }

  async clickAbout(): Promise<void> {
    await this.aboutMenuItem.click();
  }

  async getErrorMessage(): Promise<string | null> {
    return await this.errorMessage.innerText();
  }

  async isErrorMessageVisible(): Promise<boolean> {
    return await this.errorMessage.isVisible();
  }

  async waitForErrorMessage(): Promise<void> {
    await this.errorMessage.waitFor({ state: "visible", timeout: 10000 });
  }

  async getMenuItemItems(): Promise<string[]> {
    const menuItems = await this.page
      .locator(".bm-item-list a")
      .allTextContents();
    return menuItems.map((item) => item.trim());
  }

  async isMenuOpen(): Promise<boolean> {
    return await this.menuOverlay.isVisible();
  }

  getMenuItemsLocator() {
    return this.page.locator(".bm-item-list a");
  }

  async getMenuItemCount(): Promise<number> {
    return await this.getMenuItemsLocator().count();
  }

  async isInventoryLinkVisible(): Promise<boolean> {
    return await this.page.locator('[data-test="inventory-sidebar-link"]').isVisible();
  }

  async isAboutLinkVisible(): Promise<boolean> {
    return await this.page.locator('[data-test="about-sidebar-link"]').isVisible();
  }

  async isLogoutLinkVisible(): Promise<boolean> {
    return await this.page.locator('[data-test="logout-sidebar-link"]').isVisible();
  }
}
