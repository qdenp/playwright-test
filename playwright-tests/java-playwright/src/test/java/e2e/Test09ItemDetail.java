package e2e;

import com.microsoft.playwright.*;
import pageobjects.InventoryPage;
import pageobjects.ItemDetailPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Item detail - viewing and returning
 */
class Test09ItemDetail extends BaseTest {

    @Test
    void itemDetailViewingAndReturning(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        ItemDetailPage itemDetailPage = authenticatedPage.itemDetailPage;

        // Clicking a product name opens the item detail page for that product
        inventoryPage.clickProductName(0);
        itemDetailPage.waitForPageLoad();
        Assertions.assertTrue(itemDetailPage.getDetailDescriptionLocator().isVisible());

        String firstDescription = itemDetailPage.getDetailDescription();
        Assertions.assertTrue(!firstDescription.isEmpty());

        // Back to products returns to the catalogue
        itemDetailPage.clickBackToProducts();
        Assertions.assertTrue(inventoryPage.getProductNameLocator(0).isVisible());

        // Opening a different product shows its own description
        inventoryPage.clickProductName(1);
        itemDetailPage.waitForPageLoad();

        String secondDescription = itemDetailPage.getDetailDescription();
        Assertions.assertTrue(!secondDescription.isEmpty());
        Assertions.assertNotEquals(secondDescription, firstDescription);

        itemDetailPage.clickBackToProducts();
        Assertions.assertTrue(inventoryPage.getProductNameLocator(0).isVisible());
    }
}
