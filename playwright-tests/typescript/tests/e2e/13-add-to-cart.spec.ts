import { test, expect } from "../fixtures";
import { CartPage } from "../page-objects/CartPage";

/**
 * Adding a product to the cart
 */
test("Adding a product to the cart", async ({ authenticatedPage }) => {
  const { inventoryPage, cartPage } = authenticatedPage;

  // Add the first product - badge shows 1
  await inventoryPage.addProductToCart(0);
  await expect(inventoryPage.cartBadgeCount).toHaveText("1");

  // Open the cart and verify the first product
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(1);

  const firstCartNames = await cartPage.getCartItemNames();
  expect(firstCartNames[0]).toBeTruthy();
  expect(firstCartNames[0]!.length).toBeGreaterThan(0);

  // Return to the inventory page
  await cartPage.continueShopping();
  await inventoryPage.waitForPageLoad();

  // Add a second product - badge shows 2
  await inventoryPage.addProductToCart(1);
  await expect(inventoryPage.cartBadgeCount).toHaveText("2");

  // Open the cart and verify both products
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(2);

  const bothCartNames = await cartPage.getCartItemNames();
  expect(bothCartNames[1]).toBeTruthy();
  expect(bothCartNames[1]!.length).toBeGreaterThan(0);
  // The products must be different
  expect(bothCartNames[0]).not.toBe(bothCartNames[1]);
});
