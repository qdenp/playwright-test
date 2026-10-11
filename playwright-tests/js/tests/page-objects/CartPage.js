/**
 * Page Object для корзины
 */
export class CartPage {
  constructor(page) {
    this.page = page;
    this.cartItems = page.locator(".cart_item");
    this.removeItemButtons = page.locator('button:has-text("Remove")');
    this.checkoutButton = page.locator(".checkout_button");
    this.continueShoppingButton = page.locator('[data-test="continue-shopping"]');
    this.totalPrice = page.locator(".summary_total_label");
    this.quantityInput = page.locator('[data-test="quantity"]');
  }

  async waitForPageLoad() {
    await this.cartItems.first().waitFor({ state: "visible", timeout: 10000 });
  }

  async getCartItemNames() {
    const names = [];
    const count = await this.cartItems.count();
    for (let i = 0; i < count; i++) {
      const name = await this.cartItems
        .nth(i)
        .locator('[data-test="inventory-item-name"]')
        .textContent();
      names.push(name);
    }
    return names;
  }

  async changeItemQuantity(index, quantity) {
    const item = this.cartItems.nth(index);
    await item.locator('[data-test="quantity"]').fill(String(quantity));
  }

  async getRemoveButtonCount() {
    return this.removeItemButtons.count();
  }

  async removeItem(index) {
    const cartItem = this.cartItems.nth(index);
    const removeButton = cartItem.locator('button:has-text("Remove")');
    await removeButton.click();
    try {
      await cartItem.waitFor({ state: "detached", timeout: 15000 });
    } catch {
      // Element may not detach quickly
    }
  }

  async checkout() {
    await this.checkoutButton.click();
  }

  async getTotalPrice() {
    return this.totalPrice.textContent();
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
