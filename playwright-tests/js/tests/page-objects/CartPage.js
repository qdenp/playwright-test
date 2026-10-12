/**
 * Page Object for the cart page
 */
import { timeouts } from "../config.js";

export class CartPage {
  constructor(page) {
    this.page = page;
    this.cartItems = page.locator(".cart_item");
    this.checkoutButton = page.locator(".checkout_button");
    this.continueShoppingButton = page.locator('[data-test="continue-shopping"]');
    this.quantityInputs = this.cartItems.locator("input");
    this.itemQuantities = page.locator('[data-test="item-quantity"]');
  }

  /**
   * Wait for the cart page to load.
   *
   * Wait for the URL instead of the items: on an empty cart `.cart_item`
   * is absent, so waiting on `.first()` would hang until timeout.
   */
  async waitForPageLoad() {
    await this.page.waitForURL(/.*cart\.html/, { timeout: timeouts.NAVIGATION });
  }

  /** Prices of the individual items in the cart, in order. */
  async getCartItemPrices() {
    const texts = await this.cartItems
      .locator('[data-test="inventory-item-price"]')
      .allTextContents();
    return texts.map((text) => CartPage.parseMoney(text));
  }

  async getCartItemNames() {
    const names = [];
    const count = await this.cartItems.count();
    for (let i = 0; i < count; i++) {
      const name = await this.cartItems
        .nth(i)
        .locator('[data-test="inventory-item-name"]')
        .textContent();
      names.push(name || "");
    }
    return names;
  }

  async removeItem(index) {
    const cartItem = this.cartItems.nth(index);
    const removeButton = cartItem.locator('button:has-text("Remove")');
    await removeButton.click();
  }

  async checkout() {
    await this.checkoutButton.click();
  }

  async continueShopping() {
    await this.continueShoppingButton.click();
  }

  async getCartItemCount() {
    return this.cartItems.count();
  }

  async clearCart() {
    const count = await this.cartItems.count();
    for (let i = count - 1; i >= 0; i--) {
      await this.removeItem(i);
    }
  }

  static parseMoney(text) {
    if (text === null) {
      throw new Error("No amount found for the cart item");
    }
    const match = text.match(/\$\s*([\d,]+\.\d{2})/);
    if (!match) {
      throw new Error(`Could not parse the amount from "${text}"`);
    }
    return parseFloat(match[1].replace(/,/g, ""));
  }
}
