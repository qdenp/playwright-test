import { test, expect } from "../fixtures";
import { LoginPage } from "../page-objects/LoginPage";
import { InventoryPage } from "../page-objects/InventoryPage";
import { CartPage } from "../page-objects/CartPage";

/**
 * Adding a product to the cart
 */
test("Adding a product to the cart", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);
  const cartPage = new CartPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Add the first product - badge shows 1
  await inventoryPage.addProductToCart(0);
  await expect(inventoryPage.cartBadgeCount).toHaveText("1");

  // Open the cart and verify the first product
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(1);

  const firstCartNames = await cartPage.getCartItemNames();
  expect(firstCartNames[0]).toBeTruthy();
  expect(firstCartNames[0].length).toBeGreaterThan(0);

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
  expect(bothCartNames[1]).not.toHaveLength(0);
  // The products must be different
  expect(bothCartNames[0]).not.toBe(bothCartNames[1]);
});
