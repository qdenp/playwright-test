/**
 * Page Object for the item detail page.
 * URL pattern: /inventory.html?v=<product-id>
 */
export class ItemDetailPage {
  constructor(page) {
    this.page = page;
    this.detailDescription = page.locator(".inventory_details_desc");
    this.backToProducts = page.locator('[data-test="back-to-products"]');
  }

  async waitForPageLoad() {
    await this.detailDescription.waitFor({ state: "visible" });
  }

  async getDetailDescription() {
    return (await this.detailDescription.textContent()) ?? "";
  }

  async clickBackToProducts() {
    await this.backToProducts.click();
  }
}
