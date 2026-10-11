/**
 * Page Object для страницы товаров (Inventory)
 */
export class InventoryPage {
  constructor(page) {
    this.page = page;
    this.products = page.locator('.inventory_item');
    this.sortDropdown = page.locator('.product_sort_container');
    this.cartBadge = page.locator('.shopping_cart_link');
    this.cartIcon = page.locator('.shopping_cart_link');
    this.headerTitle = page.locator('.title');
  }

  async waitForPageLoad() {
    await this.headerTitle.waitFor({ state: 'visible', timeout: 10000 });
  }

  async getProductCount() {
    return this.products.count();
  }

  async getProductElement(index) {
    return this.products.nth(index);
  }

  async addProductToCart(index) {
    const product = this.products.nth(index);
    await product.locator('.btn.btn_primary.btn_small.btn_inventory').click();
  }

  async addProductByName(productName) {
    const product = this.page.locator(`.inventory_item:has-text("${productName}")`);
    await product.locator('.btn.btn_primary.btn_small.btn_inventory').click();
  }

  async getProductPrice(index) {
    const product = this.products.nth(index);
    const priceText = await product.locator('.inventory_item_price').textContent();
    return parseFloat(priceText.replace('$', ''));
  }

  async getProductTitle(index) {
    const product = this.products.nth(index);
    return await product.locator('.inventory_item_name').textContent();
  }

  async getProductDescription(index) {
    const product = this.products.nth(index);
    return await product.locator('.inventory_item_desc').textContent();
  }

  async getCartBadgeCount() {
    const badge = this.cartBadge.locator('.shopping_cart_badge');
    const isVisible = await badge.isVisible();
    if (!isVisible) {
      return 0;
    }
    const text = await badge.textContent();
    return parseInt(text, 10) || 0;
  }

  async openCart() {
    await this.cartBadge.click();
  }

  async sortByPriceLowToHigh() {
    await this.sortDropdown.selectOption('Price (low to high)');
  }

  async sortByPriceHighToLow() {
    await this.sortDropdown.selectOption('Price (high to low)');
  }

  async sortByAlphaAtoZ() {
    await this.sortDropdown.selectOption('Name (A to Z)');
  }

  async sortByAlphaZtoA() {
    await this.sortDropdown.selectOption('Name (Z to A)');
  }

  async isProductsVisible() {
    return await this.products.count() > 0;
  }

  async getTitleText() {
    return await this.headerTitle.textContent();
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

  async openHamburgerMenu() {
    await this.page.locator('.bm-burger-button').click();
  }

  async clickResetAppState() {
    await this.page.getByText('Reset App State').click();
  }

  async clickLogout() {
    await this.page.getByText('Logout').click();
  }

  async clickBackToProducts() {
    await this.page.getByText('Back to Products').click();
  }
}
