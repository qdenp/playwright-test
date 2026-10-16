import { test, expect } from "../fixtures";
import { EXPECTED_PRODUCT_COUNT } from "../config";

/**
 * Adding and removing all products
 */
test("Adding and removing all products", async ({ authenticatedPage }) => {
  const { inventoryPage, cartPage } = authenticatedPage;

  await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);

  // Add all products
  for (let i = 0; i < EXPECTED_PRODUCT_COUNT; i++) {
    await inventoryPage.addProductToCart(i);
  }

  await expect(inventoryPage.cartBadgeCount).toHaveText(
    String(EXPECTED_PRODUCT_COUNT),
  );

  // Go to the cart
  await inventoryPage.openCart();
  await cartPage.waitForPageLoad();
  await expect(cartPage.cartItems).toHaveCount(EXPECTED_PRODUCT_COUNT);

  // Remove all products
  for (let i = 0; i < EXPECTED_PRODUCT_COUNT; i++) {
    await cartPage.removeItem(0);
    await expect(cartPage.cartItems).toHaveCount(
      EXPECTED_PRODUCT_COUNT - 1 - i,
    );
  }

  // The cart must be empty
  await expect(cartPage.cartItems).toHaveCount(0);

  // Reload in order to verify the badge
  await authenticatedPage.page.reload();
  await expect(inventoryPage.cartBadgeCount).toHaveCount(0);
});
