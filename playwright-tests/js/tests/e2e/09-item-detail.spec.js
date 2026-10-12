import { test, expect } from "../fixtures.js";
import { LoginPage } from "../page-objects/LoginPage.js";
import { InventoryPage } from "../page-objects/InventoryPage.js";

/**
 * Item detail - viewing and returning
 */
test("Item detail - viewing and returning", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);

  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();

  // Clicking a product name opens the detail panel
  await inventoryPage.clickProductName(0);

  // The detail panel appears inline - verify the description is visible
  await expect(inventoryPage.getDetailDescriptionLocator("carry.allTheThings()")).toBeVisible({
    timeout: 5000,
  });

  // Verify the Back to Products button is present
  await expect(inventoryPage.getBackToProductsLocator()).toBeVisible({
    timeout: 5000,
  });

  // Return to the Products page
  await inventoryPage.clickBackToProducts();
  await expect(inventoryPage.getProductNameLocator(0)).toBeVisible({
    timeout: 5000,
  });

  // Open the details of the second product
  await inventoryPage.clickProductName(1);

  // Verify the second product's detail panel
  await expect(inventoryPage.getDetailDescriptionLocator("carry.allTheThings()")).not.toBeVisible({
    timeout: 5000,
  });
  await expect(inventoryPage.getBackToProductsLocator()).toBeVisible({
    timeout: 5000,
  });

  // Return to the Products page
  await inventoryPage.clickBackToProducts();
  await expect(inventoryPage.getProductNameLocator(0)).toBeVisible({
    timeout: 5000,
  });
});
