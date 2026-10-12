import { test, expect } from "../fixtures";

/**
 * Specific item combinations (3 of 6 products)
 */
test.describe("Item Combinations", () => {
  test("should add cheapest 3 products and verify cart", async ({
    authenticatedPage,
  }) => {
    const { inventoryPage, cartPage } = authenticatedPage;

    // Record the catalog price of every product before picking the cheapest
    const catalogPrices = await inventoryPage.getAllProductPrices();
    const cheapestThree = [...catalogPrices].sort((a, b) => a - b).slice(0, 3);

    // Add 3 cheapest: Onesie (7.99), Bike Light (9.99), Red T-Shirt (15.99)
    await inventoryPage.addProductToCart(4); // Onesie - $7.99
    await inventoryPage.addProductToCart(1); // Bike Light - $9.99
    await inventoryPage.addProductToCart(5); // Red T-Shirt - $15.99

    await expect(inventoryPage.cartBadgeCount).toHaveText("3");

    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();
    await expect(cartPage.cartItems).toHaveCount(3);

    const cartItems = await cartPage.getCartItemNames();
    expect(cartItems).toContain("Sauce Labs Onesie");
    expect(cartItems).toContain("Sauce Labs Bike Light");
    expect(cartItems).toContain("Test.allTheThings() T-Shirt (Red)");

    // The three cheapest in the catalogue are the three items in the cart
    const pricesInCart = (await cartPage.getCartItemPrices()).sort((a, b) => a - b);
    expect(pricesInCart).toEqual(cheapestThree);
  });

  test("should add most expensive 3 products and verify cart", async ({
    authenticatedPage,
  }) => {
    const { inventoryPage, cartPage } = authenticatedPage;

    const catalogPrices = await inventoryPage.getAllProductPrices();
    const priciestThree = [...catalogPrices].sort((a, b) => b - a).slice(0, 3);

    // Add 3 most expensive: Fleece Jacket (49.99), Backpack (29.99), Bolt T-Shirt (24.99)
    await inventoryPage.addProductToCart(3); // Fleece Jacket - $49.99
    await inventoryPage.addProductToCart(0); // Backpack - $29.99
    await inventoryPage.addProductToCart(2); // Bolt T-Shirt - $24.99

    await expect(inventoryPage.cartBadgeCount).toHaveText("3");

    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();
    await expect(cartPage.cartItems).toHaveCount(3);

    const cartItems = await cartPage.getCartItemNames();
    expect(cartItems).toContain("Sauce Labs Fleece Jacket");
    expect(cartItems).toContain("Sauce Labs Backpack");
    expect(cartItems).toContain("Sauce Labs Bolt T-Shirt");

    const pricesInCart = (await cartPage.getCartItemPrices()).sort((a, b) => b - a);
    expect(pricesInCart).toEqual(priciestThree);
  });

  test("should add alternating products (1st, 3rd, 5th)", async ({
    authenticatedPage,
  }) => {
    const { inventoryPage, cartPage } = authenticatedPage;

    // Read the catalog prices before leaving the inventory page
    const catalogPrices = await inventoryPage.getAllProductPrices();
    const alternating = [catalogPrices[0], catalogPrices[2], catalogPrices[4]].sort(
      (a, b) => a - b,
    );

    // Add items at indices 0, 2, 4 (Backpack, Bolt T-Shirt, Onesie)
    await inventoryPage.addProductToCart(0);
    await inventoryPage.addProductToCart(2);
    await inventoryPage.addProductToCart(4);

    await expect(inventoryPage.cartBadgeCount).toHaveText("3");

    await inventoryPage.openCart();
    await cartPage.waitForPageLoad();
    await expect(cartPage.cartItems).toHaveCount(3);

    const cartItems = await cartPage.getCartItemNames();
    expect(cartItems).toContain("Sauce Labs Backpack");
    expect(cartItems).toContain("Sauce Labs Bolt T-Shirt");
    expect(cartItems).toContain("Sauce Labs Onesie");

    // Alternating means the odd-indexed catalog entries were picked
    const pricesInCart = (await cartPage.getCartItemPrices()).sort((a, b) => a - b);
    expect(pricesInCart).toEqual(alternating);
  });
});
