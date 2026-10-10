import { Page } from '@playwright/test';

export interface Product {
  name: string;
  price: number;
  description: string;
}

export class InventoryPage {
  private page: Page;

  // Locators - инициализируются в constructor
  products: ReturnType<Page['locator']>;
  sortDropdown: ReturnType<Page['locator']>;
  cartBadge: ReturnType<Page['locator']>;
  cartIcon: ReturnType<Page['locator']>;
  headerTitle: ReturnType<Page['locator']>;

  constructor(page: Page) {
    this.page = page;
    this.products = this.page.locator('.inventory_item');
    this.sortDropdown = this.page.locator('.product_sort_container');
    this.cartBadge = this.page.locator('.shopping_cart_link');
    this.cartIcon = this.page.locator('.shopping_cart_link');
    this.headerTitle = this.page.locator('.title');
  }

  async waitForPageLoad(): Promise<void> {
    await this.headerTitle.waitFor({ state: 'visible', timeout: 10000 });
  }

  async getProductCount(): Promise<number> {
    return await this.products.count();
  }

  async getProductElement(index: number) {
    return this.products.nth(index);
  }

  async addProductToCart(index: number): Promise<void> {
    const product = this.products.nth(index);
    await product.locator('.btn.btn_primary.btn_small.btn_inventory').click();
  }

  async addProductByName(productName: string): Promise<void> {
    const product = this.page.locator(`.inventory_item:has-text("${productName}")`);
    await product.locator('.btn.btn_primary.btn_small.btn_inventory').click();
  }

  async getProductPrice(index: number): Promise<number> {
    const product = this.products.nth(index);
    const priceText = await product.locator('.inventory_item_price').textContent();
    return parseFloat(priceText.replace('$', ''));
  }

  async getProductTitle(index: number): Promise<string> {
    const product = this.products.nth(index);
    return await product.locator('.inventory_item_name').textContent();
  }

  async getProductDescription(index: number): Promise<string> {
    const product = this.products.nth(index);
    return await product.locator('.inventory_item_desc').textContent();
  }

  async getCartBadgeCount(): Promise<number> {
    const badge = this.cartBadge.locator('.shopping_cart_badge');
    const text = await badge.textContent();
    return parseInt(text, 10) || 0;
  }

  async openCart(): Promise<void> {
    await this.cartBadge.click();
  }

  async sortByPriceLowToHigh(): Promise<void> {
    await this.sortDropdown.selectOption('Price (low to high)');
  }

  async sortByPriceHighToLow(): Promise<void> {
    await this.sortDropdown.selectOption('Price (high to low)');
  }

  async sortByAlphaAtoZ(): Promise<void> {
    await this.sortDropdown.selectOption('Name (A to Z)');
  }

  async sortByAlphaZtoA(): Promise<void> {
    await this.sortDropdown.selectOption('Name (Z to A)');
  }

  async isProductsVisible(): Promise<boolean> {
    return await this.products.count() > 0;
  }

  async getTitleText(): Promise<string> {
    return await this.headerTitle.textContent();
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
        description: await this.getProductDescription(i)
      });
    }
    return products;
  }
}
