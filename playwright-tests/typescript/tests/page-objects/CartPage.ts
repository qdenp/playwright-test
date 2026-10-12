import { Page } from "@playwright/test";
import { timeouts } from "../config";

export class CartPage {
  private page: Page;

  // Locators - initialised in the constructor
  cartItems: ReturnType<Page["locator"]>;
  checkoutButton: ReturnType<Page["locator"]>;
  continueShoppingButton: ReturnType<Page["locator"]>;

  constructor(page: Page) {
    this.page = page;
    this.cartItems = this.page.locator(".cart_item");
    this.checkoutButton = this.page.locator(".checkout_button");
    this.continueShoppingButton = this.page.locator(
      '[data-test="continue-shopping"]',
    );
  }

  /**
   * Wait for the cart page to load.
   *
   * Wait for the URL instead of the items: on an empty cart `.cart_item`
   * is absent, so waiting on `.first()` would hang until timeout.
   */
  async waitForPageLoad(): Promise<void> {
    await this.page.waitForURL(/.*cart\.html/, { timeout: timeouts.NAVIGATION });
  }

  async getCartItemNames(): Promise<string[]> {
    const names: string[] = [];
    const count = await this.cartItems.count();
    for (let i = 0; i < count; i++) {
      const name = await this.cartItems
        .nth(i)
        .locator('[data-test="inventory-item-name"]')
        .innerText();
      names.push(name);
    }
    return names;
  }

  async removeItem(index: number): Promise<void> {
    const cartItem = this.cartItems.nth(index);
    const removeButton = cartItem.locator('button:has-text("Remove")');
    await removeButton.click();
  }

  async checkout(): Promise<void> {
    await this.checkoutButton.click();
  }

  async continueShopping(): Promise<void> {
    await this.continueShoppingButton.click();
  }

  async getCartItemCount(): Promise<number> {
    return await this.cartItems.count();
  }

  async clearCart(): Promise<void> {
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
    