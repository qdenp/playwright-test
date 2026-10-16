/**
 * Verify quantity stays at 1 per item (Sauce Demo doesn't allow editing quantity)
 */
import { test, expect } from "../fixtures";

test.describe("Quantity and Race Conditions", () => {
  test("should show quantity 1 per item and offer no way to edit it", async ({
    authenticatedPage,
  }) => {
    const { page, inventoryPage, cartPage } = authenticatedPage;

    await inventoryPage.addProductToCart(0); // Backpack
    await inventoryPage.addProductToCart(2); // Bolt T-Shirt
    await inventoryPage.addProductToCart(4); // Onesie

    await expect(inventoryPage.cartBadgeCount).toHaveText("3");

    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();

    // Every item is displayed with quantity 1
    await expect(cartPage.cartItems).toHaveCount(3);
    await expect(cartPage.itemQuantities).toHaveText(["1", "1", "1"]);

    // There is no quantity input to edit it with
    await expect(cartPage.quantityInputs).toHaveCount(0);
  });

  test("should add every product when the add buttons are clicked concurrently", async ({
    authenticatedPage,
  }) => {
    const { page, inventoryPage, cartPage } = authenticatedPage;

    const buttonCount = await inventoryPage.addToCartButtons.count();

    // Click all add-to-cart buttons without awaiting each one individually
    await page.evaluate((count) => {
      const buttons = Array.from(
        document.querySelectorAll(".btn.btn_primary.btn_small.btn_inventory"),
      );
      buttons.slice(0, count).forEach((button) => button.click());
    }, buttonCount);

    // All of them must have landed in the cart
    await expect(inventoryPage.cartBadgeCount).toHaveText(String(buttonCount));

    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();
    await expect(cartPage.cartItems).toHaveCount(buttonCount);

    // And none of them can have a quantity other than 1
    await expect(cartPage.itemQuantities).toHaveText(
      Array.from({ length: buttonCount }, () => "1"),
    );
  });
});
