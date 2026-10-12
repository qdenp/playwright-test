/**
 * Проверка карточек товаров на главной
 */
import { test, expect } from "../fixtures.js";
import { EXPECTED_PRODUCT_COUNT } from "../config.js";

test("Проверка карточек товаров на главной", async ({ authenticatedPage }) => {
  const { inventoryPage } = authenticatedPage;

  // На каждой карточке есть название, описание, цена и кнопка добавления в корзину
  await expect(inventoryPage.products).toHaveCount(EXPECTED_PRODUCT_COUNT);
  await expect(inventoryPage.productNames).toHaveCount(EXPECTED_PRODUCT_COUNT);
  await expect(inventoryPage.productDescriptions).toHaveCount(EXPECTED_PRODUCT_COUNT);
  await expect(inventoryPage.productPrices).toHaveCount(EXPECTED_PRODUCT_COUNT);
  await expect(inventoryPage.addToCartButtons).toHaveCount(EXPECTED_PRODUCT_COUNT);

  // Все элементы непустые и видимые
  await expect(inventoryPage.productNames.first()).not.toBeEmpty();
  await expect(inventoryPage.productDescriptions.first()).not.toBeEmpty();
  await expect(inventoryPage.productPrices.first()).toContainText("$");
  await expect(inventoryPage.addToCartButtons.first()).toBeVisible();
});
