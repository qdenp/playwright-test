import { Page } from "@playwright/test";

/**
 * Page Object for the item detail page.
 * URL pattern: /inventory.html?v=<product-id>
 */
export class ItemDetailPage {
  private page: Page;

  // Locators
  detailDescription: ReturnType<Page["locator"]>;
  backToProducts: ReturnType<Page["locator"]>;

  constructor(page: Page) {
    this.page = page;
    this.detailDescription = this.page.locator(".inventory_details_desc");
    this.backToProducts = this.page.locator('[data-test="back-to-products"]');
  }

  async waitForPageLoad(): Promise<void> {
    await this.detailDescription.waitFor({ state: "visible" });
  }

  async getDetailDescription(): Promise<string> {
    return (await this.detailDescription.textContent()) ?? "";
  }

  async clickBackToProducts(): Promise<void> {
    await this.backToProducts.click();
  }
}
