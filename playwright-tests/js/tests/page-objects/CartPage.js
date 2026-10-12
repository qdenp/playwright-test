/**
 * Page Object для корзины
 */
import { timeouts } from "../config.js";

export class CartPage {
  constructor(page) {
    this.page = page;
    this.cartItems = page.locator(".cart_item");
    this.checkoutButton = page.locator(".checkout_button");
    this.continueShoppingButton = page.locator('[data-test="continue-shopping"]');
  }

  async waitForPageLoad() {
    await this.cartItems.first().waitFor({ state: "visible", timeout: timeouts.DEFAULT });
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
      try {
        await this.removeItem(i);
      } catch {
        break;
      }
    }
  }
}
