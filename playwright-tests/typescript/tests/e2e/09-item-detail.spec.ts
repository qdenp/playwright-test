import { test, expect } from "../fixtures";

test("Item detail - viewing and returning", async ({ authenticatedPage }) => {
  const { inventoryPage } = authenticatedPage;

  // Clicking a product name opens the item detail page for that product
  await inventoryPage.clickProductName(0);
  await expect(inventoryPage.getDetailDescriptionLocator()).toBeVisible();
  await expect(inventoryPage.getBackToProductsLocator()).toBeVisible();

  const firstDescription = await inventoryPage
    .getDetailDescriptionLocator()
    .innerText();
  expect(firstDescription).toBeTruthy();

  // Back to products returns to the catalogue
  await inventoryPage.clickBackToProducts();
  await expect(inventoryPage.getProductNameLocator(0)).toBeVisible();

  // Opening a different product shows its own description
  await inventoryPage.clickProductName(1);
  await expect(inventoryPage.getDetailDescriptionLocator()).toBeVisible();

  const secondDescription = await inventoryPage
    .getDetailDescriptionLocator()
    .innerText();
  expect(secondDescription).toBeTruthy();
  expect(secondDescription).not.toBe(firstDescription);

  await expect(inventoryPage.getBackToProductsLocator()).toBeVisible();
  await inventoryPage.clickBackToProducts();
  await expect(inventoryPage.getProductNameLocator(0)).toBeVisible();
});
