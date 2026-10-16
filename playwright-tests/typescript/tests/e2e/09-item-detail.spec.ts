import { test, expect } from "../fixtures";

test("Item detail - viewing and returning", async ({ authenticatedPage }) => {
  const { inventoryPage, itemDetailPage } = authenticatedPage;

  // Clicking a product name opens the item detail page for that product
  await inventoryPage.clickProductName(0);
  await itemDetailPage.waitForPageLoad();
  await expect(itemDetailPage.detailDescription).toBeVisible();

  const firstDescription = await itemDetailPage.getDetailDescription();
  expect(firstDescription).toBeTruthy();

  // Back to products returns to the catalogue
  await itemDetailPage.clickBackToProducts();
  await expect(inventoryPage.getProductNameLocator(0)).toBeVisible();

  // Opening a different product shows its own description
  await inventoryPage.clickProductName(1);
  await itemDetailPage.waitForPageLoad();

  const secondDescription = await itemDetailPage.getDetailDescription();
  expect(secondDescription).toBeTruthy();
  expect(secondDescription).not.toBe(firstDescription);

  await itemDetailPage.clickBackToProducts();
  await expect(inventoryPage.getProductNameLocator(0)).toBeVisible();
});
