import { test, expect } from "../fixtures.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";

/**
 * Adding and removing all products
 */
test("Adding and removing all products", async ({ authenticatedPage }) => {
  const { inventoryPage, cartPage } = authenticatedPage;

  const productCount = await inventoryPage.getProductCount();
  await expect(inventoryPage.products).toHaveCount(productCount);

  // Add all products
  for (let i = 0; i < productCount; i++) {
    await inventoryPage.addProductToCart(i);
  }

  await expect(inventoryPage.cartBadgeCount).toHaveText(String(productCount));

  // Go to the cart
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(productCount);

  // Remove all products
  for (let i = 0; i < productCount; i++) {
    await cartPage.removeItem(0);
    await expect(cartPage.cartItems).toHaveCount(productCount - 1 - i);
  }

  // The cart must be empty
  await expect(cartPage.cartItems).toHaveCount(0);

  // Reload in order to verify the badge
  await authenticatedPage.page.reload();
  await expect(inventoryPage.cartBadgeCount).toHaveCount(0);
});
