import { test, expect } from "../fixtures";
import { CartPage } from "../page-objects/CartPage.js";
import { timeouts } from "../config";

/**
 * Test 34: Reset App State
 */
test.describe("Reset App State", () => {
  test("01 - should reset cart via Reset App State in hamburger menu", async ({ authenticatedPage }) => {
    const { inventoryPage } = authenticatedPage;

    await inventoryPage.waitForPageLoad();

    // Добавляем товары и сбрасываем корзину через Reset App State
    for (let i = 0; i < 3; i++) {
      await inventoryPage.addProductToCart(i);
    }
    await expect(inventoryPage.cartBadgeCount).toHaveText("3");

    await inventoryPage.openHamburgerMenu();
    await inventoryPage.resetAppState();
    await inventoryPage.closeHamburgerMenu();

    // Корзина пуста — badge отсутствует
    await expect(inventoryPage.cartBadgeCount).toHaveCount(0);
  });

  test("02 - should reset app state and verify complete reset", async ({ authenticatedPage }) => {
    const { inventoryPage } = authenticatedPage;

    await inventoryPage.waitForPageLoad();

    for (let i = 0; i < 3; i++) {
      await inventoryPage.addProductToCart(i);
    }
    await expect(inventoryPage.cartBadgeCount).toHaveText("3");

    await inventoryPage.openHamburgerMenu();
    await inventoryPage.resetAppState();
    await inventoryPage.closeHamburgerMenu();

    await expect(inventoryPage.cartBadgeCount).toHaveCount(0);
  });
});
