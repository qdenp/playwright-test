import { Page } from "@playwright/test";
import { timeouts } from "../config";

export interface Product {
  name: string;
  price: number;
  description: string;
}

export class InventoryPage {
  private page: Page;

  // Locators - initialised in the constructor
  products: ReturnType<Page["locator"]>;
  sortDropdown: ReturnType<Page["locator"]>;
  cartBadge: ReturnType<Page["locator"]>;
  cartBadgeCount: ReturnType<Page["locator"]>;
  headerTitle: ReturnType<Page["locator"]>;
  inventoryLink: ReturnType<Page["locator"]>;
  aboutLink: ReturnType<Page["locator"]>;
  logoutLink: ReturnType<Page["locator"]>;
  productNames: ReturnType<Page["locator"]>;
  productDescriptions: ReturnType<Page["locator"]>;
  productPrices: ReturnType<Page["locator"]>;
  productImages: ReturnType<Page["locator"]>;
  addToCartButtons: ReturnType<Page["locator"]>;

  constructor(page: Page) {
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
  }

  async goTo(): Promise<void> {
    await this.page.goto("/inventory.html", { waitUntil: "domcontentloaded", timeout: timeouts.NAVIGATION });
  }

  async clickAbout(): Promise<void> {
    await this.aboutLink.click();
  }

  async waitForPageLoad(): Promise<void> {
    await this.headerTitle.waitFor({ state: "visible", timeout: 10000 });
  }

  async getProductCount(): Promise<number> {
    return await this.products.count();
  }

  async getProductElement(index: number) {
    return this.products.nth(index);
  }

  async addProductToCart(index: number): Promise<void> {
    const product = this.products.nth(index);
    await product.locator(".btn.btn_primary.btn_small.btn_inventory").click();
  }

  async addProductByName(productName: string): Promise<void> {
    const product = this.page.locator(
      `.inventory_item:has-text("${productName}")`,
    );
    await product.locator(".btn.btn_primary.btn_small.btn_inventory").click();
  }

  async getProductPrice(index: number): Promise<number> {
    const product = this.products.nth(index);
    const priceText = await product
      .locator(".inventory_item_price")
      .textContent();
    if (!priceText)
      throw new Error(`Price not found for product at index ${index}`);
    return parseFloat(priceText.replace("$", ""));
  }

  async getProductTitle(index: number): Promise<string> {
    const product = this.products.nth(index);
    const title = await product.locator(".inventory_item_name").textContent();
    return title ?? "";
  }

  async getProductDescription(index: number): Promise<string> {
    const product = this.products.nth(index);
    const desc = await product.locator(".inventory_item_desc").textContent();
    return desc ?? "";
  }

  async getCartBadgeCount(): Promise<number> {
    const badge = this.cartBadgeCount;
    const isVisible = await badge.isVisible();
    if (!isVisible) {
      return 0;
    }
    const text = await badge.textContent();
    return parseInt(text ?? "0", 10) || 0;
  }

  async openCart(): Promise<void> {
    await this.cartBadge.click();
    await this.page.waitForURL("**cart*", { timeout: timeouts.DEFAULT });
  }

  async sortByPriceLowToHigh(): Promise<void> {
    await this.sortDropdown.selectOption("Price (low to high)");
  }

  async sortByPriceHighToLow(): Promise<void> {
    await this.sortDropdown.selectOption("Price (high to low)");
  }

  async sortByAlphaAtoZ(): Promise<void> {
    await this.sortDropdown.selectOption("Name (A to Z)");
  }

  async sortByAlphaZtoA(): Promise<void> {
    await this.sortDropdown.selectOption("Name (Z to A)");
  }

  async isProductsVisible(): Promise<boolean> {
    return (await this.products.count()) > 0;
  }

  async getTitleText(): Promise<string> {
    const title = await this.headerTitle.textContent();
    return title ?? "";
  }

  async getAllProductNames(): Promise<string[]> {
    const names: string[] = [];
    const count = await this.getProductCount();
    for (let i = 0; i < count; i++) {
      names.push(await this.getProductTitle(i));
    }
    return names;
  }

  async getAllProductPrices(): Promise<number[]> {
    const prices: number[] = [];
    const count = await this.getProductCount();
    for (let i = 0; i < count; i++) {
      prices.push(await this.getProductPrice(i));
    }
    return prices;
  }

  async getAllProducts(): Promise<Product[]> {
    const products: Product[] = [];
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

  // Methods for working with product detail panels
  async clickProductName(index: number): Promise<void> {
    await this.products.nth(index).locator('[data-test="inventory-item-name"]').click();
  }

  async clickBackToProducts(): Promise<void> {
    await this.page.getByText("Back to Products").click();
  }

  getDetailDescriptionLocator(text: string) {
    return this.page.getByText(text);
  }

  getBackToProductsLocator() {
    return this.page.getByText("Back to Products");
  }

  getProductNameLocator(index: number) {
    return this.products.nth(index).locator('[data-test="inventory-item-name"]');
  }

  // Methods for working with the hamburger menu
  async openHamburgerMenu(): Promise<void> {
    await this.page.locator(".bm-burger-button").click();
  }

  async closeHamburgerMenu(): Promise<void> {
    await this.page.locator(".bm-cross-button").click();
  }

  /** Reset the app state (the cart) via the hamburger menu */
  async resetAppState(): Promise<void> {
    await this.page.locator('[data-test="reset-sidebar-link"]').click();
  }

  async clickLogout(): Promise<void> {
    await this.logoutLink.click();
  }

  getMenuItemsLocator() {
    return this.page.locator(".bm-item-list a");
  }
}