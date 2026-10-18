package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.CartPage;
import pageobjects.InventoryPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Performance budget tests
 */
class Test27PerformanceBudget extends BaseTest {

    @Test
    void shouldLoadLoginPageWithin2Seconds(Page page) {
        long startTime = System.currentTimeMillis();
        Page.NavigateOptions navigateOptions = new Page.NavigateOptions()
                .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED);
        page.navigate(TestConfig.BASE_URL, navigateOptions);
        long loadTime = System.currentTimeMillis() - startTime;

        // Allow up to 3 seconds for slower browsers (Firefox, WebKit)
        Assertions.assertTrue(loadTime < TestConfig.LOGIN_PAGE_BUDGET);
        System.out.println("Login page loaded in " + loadTime + "ms");
    }

    @Test
    void shouldLoadInventoryPageWithin3SecondsAfterLogin(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        long startTime = System.currentTimeMillis();
        inventoryPage.waitForPageLoad();
        long loadTime = System.currentTimeMillis() - startTime;

        Assertions.assertTrue(loadTime < TestConfig.INVENTORY_PAGE_BUDGET);
        System.out.println("Inventory page loaded in " + loadTime + "ms");
    }

    @Test
    void shouldLoadAll6ProductsWithinPerformanceBudget(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        long startTime = System.currentTimeMillis();
        inventoryPage.waitForPageLoad();
        long loadTime = System.currentTimeMillis() - startTime;

        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProducts().count());
        Assertions.assertTrue(loadTime < TestConfig.PRODUCTS_LIST_BUDGET);
        System.out.println("All 6 products loaded in " + loadTime + "ms");
    }

    @Test
    void shouldLoadCartPageWithin2Seconds(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;

        long startTime = System.currentTimeMillis();
        inventoryPage.addProductToCart(0);
        inventoryPage.openCart();
        cartPage.waitForPageLoad();
        long loadTime = System.currentTimeMillis() - startTime;

        Assertions.assertTrue(loadTime < TestConfig.CART_PAGE_BUDGET);
        System.out.println("Cart page loaded in " + loadTime + "ms");
    }

    @Test
    void shouldVerifyPageLoadConsistency3Runs(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        long[] loadTimes = new long[3];
        for (int i = 0; i < 3; i++) {
            long startTime = System.currentTimeMillis();
            inventoryPage.waitForPageLoad();
            loadTimes[i] = System.currentTimeMillis() - startTime;
        }

        double avgLoadTime = (loadTimes[0] + loadTimes[1] + loadTimes[2]) / 3.0;
        System.out.println("Average load time: " + avgLoadTime + "ms");
        Assertions.assertTrue(avgLoadTime < TestConfig.AVERAGE_LOAD_BUDGET);
    }

    @Test
    void shouldVerifyProductImagesLoadWithinBudget(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        inventoryPage.waitForPageLoad();

        long startTime = System.currentTimeMillis();
        inventoryPage.getProductImages().first().waitFor(new Locator.WaitForOptions()
                .setTimeout(TestConfig.DEFAULT_TIMEOUT));
        long loadTime = System.currentTimeMillis() - startTime;

        Assertions.assertTrue(loadTime < TestConfig.IMAGES_BUDGET);
        System.out.println("Images loaded in " + loadTime + "ms");
    }
}
