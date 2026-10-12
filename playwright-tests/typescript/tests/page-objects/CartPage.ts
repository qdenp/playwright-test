import { Page } from "@playwright/test";
import { timeouts } from "../config";

export interface CartItem {
  name: string;
  price: number;
  quantity: number;
}

export class CartPage {
  private page: Page;

  // Locators - инициализируются в constructor
  cartItems: ReturnType<Page["locator"]>;
  removeItemButtons: ReturnType<Page["locator"]>;
  checkoutButton: ReturnType<Page["locator"]>;
  continueShoppingButton: ReturnType<Page["locator"]>;
  totalPrice: ReturnType<Page["locator"]>;
  quantityInput: ReturnType<Page["locator"]>;

  constructor(page: Page) {
    this.page = page;
    this.cartItems = this.page.locator(".cart_item");
    this.removeItemButtons = this.page.locator('button:has-text("Remove")');
    this.checkoutButton = this.page.locator(".checkout_button");
    this.continueShoppingButton = this.page.locator(
      '[data-test="continue-shopping"]',
    );
    this.totalPrice = this.page.locator(".summary_total_label");
    this.quantityInput = this.page.locator('[data-test="quantity"]');
  }

  async waitForPageLoad(): Promise<void> {
    await this.cartItems.first().waitFor({ state: "visible", timeout: timeouts.DEFAULT });
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

  async changeItemQuantity(index: number, quantity: number): Promise<void> {
    const item = this.cartItems.nth(index);
    await item.locator('[data-test="quantity"]').fill(String(quantity));
  }

  async getRemoveButtonCount(): Promise<number> {
    return await this.removeItemButtons.count();
  }

  async removeItem(index: number): Promise<void> {
    const cartItem = this.cartItems.nth(index);
    const removeButton = cartItem.locator('button:has-text("Remove")');
    await removeButton.click();
    // Не ждём detached — просто даём время на обновление
    await this.page.waitForTimeout(500);
  }

  async checkout(): Promise<void> {
    await this.checkoutButton.click();
  }

  async getTotalPrice(): Promise<string> {
    const price = await this.totalPrice.textContent();
    return price ?? "";
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
    