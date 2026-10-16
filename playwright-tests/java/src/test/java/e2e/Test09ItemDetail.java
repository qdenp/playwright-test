package e2e;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.InventoryPage;
import pageobjects.ItemDetailPage;

/**
 * Item detail - viewing and returning.
 */
public class Test09ItemDetail extends BaseTest {

    @Test
    @DisplayName("Item detail - viewing a product and returning to the catalogue")
    void itemDetailViewingAndReturning() {
        InventoryPage inventoryPage = loginAsStandardUser();

        // Clicking a product name opens the item detail page for that product.
        inventoryPage.clickProductName(0);
        ItemDetailPage detailPage = new ItemDetailPage(driver);
        detailPage.waitForPageLoad();
        assertTrue(detailPage.getDetailDescriptionLocator().isDisplayed(), "the detail page is shown");
        assertTrue(detailPage.getBackToProductsLocator().isDisplayed(), "the back link is shown");
        String firstDescription = detailPage.getDetailDescription();
        assertFalse(firstDescription.isEmpty(), "the description is not empty");

        // Back to products returns to the catalogue.
        detailPage.clickBackToProducts();
        inventoryPage.waitForPageLoad();
        assertTrue(inventoryPage.isFirstProductVisible());

        // Opening a different product shows its own description.
        inventoryPage.clickProductName(1);
        detailPage = new ItemDetailPage(driver);
        detailPage.waitForPageLoad();
        assertTrue(detailPage.getDetailDescriptionLocator().isDisplayed(), "the detail page is shown");
        String secondDescription = detailPage.getDetailDescription();
        assertFalse(secondDescription.isEmpty(), "the description is not empty");
        assertNotEquals(firstDescription, secondDescription, "a different product has a different description");

        assertTrue(detailPage.getBackToProductsLocator().isDisplayed());
        detailPage.clickBackToProducts();
        inventoryPage.waitForPageLoad();
        assertTrue(inventoryPage.isFirstProductVisible(), "the catalogue is back");
    }
}
