package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.CheckoutPage;
import pageobjects.InventoryPage;
import pageobjects.CartPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Responsive design tests
 */
class Test25ResponsiveDesign extends BaseTest {

    @Test
    void shouldWorkOnMobileViewport(Page page) {
        page.setViewportSize(375, 667);

        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProducts().count());

        inventoryPage.openHamburgerMenu();
        Assertions.assertTrue(inventoryPage.getMenuOverlay().isVisible());
        inventoryPage.closeHamburgerMenu();
    }

    @Test
    void shouldWorkOnTabletViewport(Page page) {
        page.setViewportSize(768, 1024);

        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProducts().count());
    }

    @Test
    void shouldHandleViewportResizeDuringInteraction(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        // Resize to mobile
        page.setViewportSize(375, 667);
        inventoryPage.addProductToCart(0);

        // Resize to tablet
        page.setViewportSize(768, 1024);
        Assertions.assertEquals("1", inventoryPage.getCartBadgeCountLocator().textContent());
    }

    @Test
    void shouldAddToCartOnMobileViewport(Page page) {
        page.setViewportSize(375, 667);

        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(1);

        Assertions.assertEquals("2", inventoryPage.getCartBadgeCountLocator().textContent());
    }

    @Test
    void shouldCheckoutOnTabletViewport(Page page) {
        page.setViewportSize(768, 1024);

        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;
        CartPage cartPage = authenticatedPage.cartPage;
        CheckoutPage checkoutPage = authenticatedPage.checkoutPage;

        inventoryPage.addProductToCart(0);
        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        cartPage.checkout();
        checkoutPage.waitForPageLoad();

        checkoutPage.fillInfo("Test", "User", "12345");
        checkoutPage.clickContinue();

        Assertions.assertTrue(checkoutPage.getFinishButton().isVisible());
    }
}
