/**
 * Page Object for the inventory page
 */
import { timeouts } from "../config";

export class InventoryPage {
  // Locators - initialised in the constructor
  products;
  sortDropdown;
  cartBadge;
  cartBadgeCount;
  headerTitle;
  inventoryLink;
  aboutLink;
  logoutLink;
  productNames;
  productDescriptions;
  productPrices;
  productImages;
  addToCartButtons;
  socialXLink;
  socialFacebookLink;
  socialLinkedinLink;
  hamburgerMenu;
  menuOverlay;
  closeButton;

  constructor(page) {
    this.page = page;
    this.products = this.page.locator(".inventory_item");
    this.sortDropdown = this.page.locator(".product_sort_container");
    this.cartBadge = this.page.locator(".shopping_cart_link");
    this.cartBadgeCount = this.page.locator(".shopping_cart_badge");
    this.headerTitle = this.page.locator(".title");
    this.inventoryLink = this.page.locator('[data-test="inventory-sidebar-link"]');
    this.aboutLink = this.page.locator('[data-test="about-sidebar-link"]');
    this.logoutLink = this.page.locator('[data-test="logout-sidebar-link"]');
    this.productNames = this.page.locator(".inventory_item_name");
    this.productDescriptions = this.page.locator(".inventory_item_desc");
    this.productPrices = this.page.locator(".inventory_item_price");
    this.productImages = this.page.locator(".inventory_item img");
    this.addToCartButtons = this.page.locator(".btn.btn_primary.btn_small.btn_inventory");
    this.socialXLink = this.page.locator('[data-test="social-x"]');
    this.socialFacebookLink = this.page.locator('[data-test="social-facebook"]');
    this.socialLinkedinLink = this.page.locator('[data-test="social-linkedin"]');
    this.hamburgerMenu = this.page.locator(".bm-burger-button");
    this.menuOverlay = this.page.locator(".bm-menu-wrap");
    this.closeButton = this.page.locator(".bm-cross-button");
  }

  async goTo() {
    await this.page.goto("inventory.html", { waitUntil: "domcontentloaded", timeout: timeouts.NAVIGATION });
  }

  async clickAbout() {
    await this.aboutLink.click();
  }

  async waitForPageLoad() {
    await this.headerTitle.waitFor({ state: "visible", timeout: timeouts.DEFAULT });
  }

  async getProductCount() {
    return await this.products.count();
  }

  getProductElement(index) {
    return this.products.nth(index);
  }

  async addProductToCart(index) {
    const product = this.products.nth(index);
    await product.locator(this.addToCartButtons).click();
  }

  async getProductPrice(index) {
    const product = this.products.nth(index);
    const priceText = await product
      .locator(".inventory_item_price")
      .textContent();
    if (!priceText)
      throw new Error(`Price not found for product at index ${index}`);
    return parseFloat(priceText.replace("$", ""));
  }

  async getProductTitle(index) {
    const product = this.products.nth(index);
    const title = await product.locator(".inventory_item_name").textContent();
    return title ?? "";
  }

  async getProductDescription(index) {
    const product = this.products.nth(index);
    const desc = await product.locator(".inventory_item_desc").textContent();
    return desc ?? "";
  }

  async getCartBadgeCount() {
    const badge = this.cartBadgeCount;
    const isVisible = await badge.isVisible();
    if (!isVisible) {
      return 0;
    }
    const text = await badge.textContent();
    return parseInt(text ?? "0", 10) || 0;
  }

  async openCart() {
    await this.cartBadge.click();
    await this.page.waitForURL("**cart*", { timeout: timeouts.DEFAULT });
  }

  async sortByPriceLowToHigh() {
    await this.sortDropdown.selectOption("Price (low to high)");
  }

  async sortByPriceHighToLow() {
    await this.sortDropdown.selectOption("Price (high to low)");
  }

  async sortByAlphaAtoZ() {
    await this.sortDropdown.selectOption("Name (A to Z)");
  }

  async sortByAlphaZtoA() {
    await this.sortDropdown.selectOption("Name (Z to A)");
  }

  async isProductsVisible() {
    return (await this.products.count()) > 0;
  }

  async getTitleText() {
    const title = await this.headerTitle.textContent();
    return title ?? "";
  }

  async getAllProductNames() {
    const names = [];
    const count = await this.getProductCount();
    for (let i = 0; i < count; i++) {
      names.push(await this.getProductTitle(i));
    }
    return names;
  }

  async getAllProductPrices() {
    const prices = [];
    const count = await this.getProductCount();
    for (let i = 0; i < count; i++) {
      prices.push(await this.getProductPrice(i));
    }
    return prices;
  }

  async getAllProducts() {
    const products = [];
    const count = await this.getProductCount();
    for (let i = 0; i < count; i++) {
      products.push({
        name: await this.getProductTitle(i),
        price: await this.getProductPrice(i),
        description: await this.getProductDescription(i),
      });
    }
    return products;
  }

  // Methods for working with the item detail page
  async clickProductName(index) {
    await this.products.nth(index).locator('[data-test="inventory-item-name"]').click();
  }

  getProductNameLocator(index) {
    return this.products.nth(index).locator('[data-test="inventory-item-name"]');
  }

  // Methods for working with the hamburger menu
  async openHamburgerMenu() {
    await this.hamburgerMenu.click();
  }

  async closeHamburgerMenu() {
    await this.closeButton.click();
  }

  /** Reset the app state (the cart) via the hamburger menu */
  async resetAppState() {
    await this.page.locator('[data-test="reset-sidebar-link"]').click();
  }

  async clickLogout() {
    await this.logoutLink.click();
  }

  getMenuItemsLocator() {
    return this.page.locator(".bm-item-list a");
  }
}
