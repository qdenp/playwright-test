import { Page, expect } from "@playwright/test";
import { timeouts } from "../config";

export class CartPage {
  private page: Page;

  // Locators - initialised in the constructor
  cartItems: ReturnType<Page["locator"]>;
  checkoutButton: ReturnType<Page["locator"]>;
  continueShoppingButton: ReturnType<Page["locator"]>;

  /** Поля ввода количества. В SauceDemo их нет — проверять на 0. */
  quantityInputs: ReturnType<Page["locator"]>;

  /** Отображаемое количество товара. Один элемент на товар, только чтение. */
  itemQuantities: ReturnType<Page["locator"]>;

  constructor(page: Page) {
    this.page = page;
    this.cartItems = this.page.locator(".cart_item");
    this.checkoutButton = this.page.locator(".checkout_button");
    this.continueShoppingButton = this.page.locator(
      '[data-test="continue-shopping"]',
    );
    this.quantityInputs = this.cartItems.locator("input");
    this.itemQuantities = this.page.locator('[data-test="item-quantity"]');
  }

  /**
   * Wait for the cart page to load.
   *
   * Wait for the URL instead of the items: on an empty cart `.cart_item`
   * is absent, so waiting on `.first()` would hang until timeout.
   */
  async waitForPageLoad(): Promise<void> {
    // Wait for cart page content or just wait for network idle
    // After removing items, SauceDemo may redirect away from cart.html
    await this.page.waitForLoadState("domcontentloaded", { timeout: timeouts.NAVIGATION });
  }

  /** Prices of the individual items in the cart, in order. */
  async getCartItemPrices(): Promise<number[]> {
    const texts = await this.cartItems
      .locator('[data-test="inventory-item-price"]')
      .allTextContents();
    return texts.map((text) => CartPage.parseMoney(text));
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
      await this.removeItem(i);
    }
  }

  private static parseMoney(text: string | null): number {
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
    