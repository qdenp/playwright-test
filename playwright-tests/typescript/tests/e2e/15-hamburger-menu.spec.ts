import { test, expect } from "../fixtures";

/**
 * Hamburger меню — открытие и закрытие
 */
test("Hamburger меню — открытие и закрытие", async ({ authenticatedPage }) => {
  const { loginPage, inventoryPage } = authenticatedPage;

  // Открываем меню
  await loginPage.openHamburgerMenu();
  await expect(loginPage.closeButton).toBeVisible();

  // Проверяем количество элементов меню (5 штук)
  await expect(inventoryPage.getMenuItemsLocator()).toHaveCount(5);

  // Проверяем наличие элементов меню
  await expect(inventoryPage.inventoryLink).toBeVisible();
  await expect(inventoryPage.aboutLink).toBeVisible();
  await expect(inventoryPage.logoutLink).toBeVisible();

  // Закрываем меню
  await loginPage.closeHamburgerMenu();
  await expect(loginPage.closeButton).not.toBeVisible();
});
