/**
 * Cart badge increments by 1 on add and decrements by 1 on remove
 */
import { test, expect } from "../fixtures";
import { InventoryPage } from "../page-objects/InventoryPage.js";
import { CartPage } from "../page-objects/CartPage.js";

test("Cart badge increments on add and decrements on remove", async ({
  authenticatedPage,
}) => {
  const { inventoryPage, cartPage } = authenticatedPage;

  // The cart starts empty - no badge
  await expect(inventoryPage.cartBadgeCount).toHaveCount(0);

  // Adding a product increments the badge by 1
  await inventoryPage.addProductToCart(0);
  await expect(inventoryPage.cartBadgeCount).toHaveText("1");

  // Adding a second product increments the badge again
  await inventoryPage.addProductToCart(1);
  await expect(inventoryPage.cartBadgeCount).toHaveText("2");

  // Going to the cart and removing one product decrements the badge
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  await cartPage.removeItem(0);
  await expect(cartPage.cartItems).toHaveCount(1);
  await expect(inventoryPage.cartBadgeCount).toHaveText("1");

  // Removing the last product clears the badge
  await cartPage.removeItem(0);
  await expect(cartPage.cartItems).toHaveCount(0);
  await expect(inventoryPage.cartBadgeCount).toHaveCount(0);
});
