package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.CartPage;
import pageobjects.CheckoutPage;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;

/**
 * Responsive design tests.
 */
public class Test25ResponsiveDesign extends BaseTest {

    private static final int MOBILE_WIDTH = 375;
    private static final int MOBILE_HEIGHT = 667;
    private static final int TABLET_WIDTH = 768;
    private static final int TABLET_HEIGHT = 1024;

    @Test
    @DisplayName("The shop works on a mobile viewport")
    void worksOnMobileViewport() {
        setViewport(MOBILE_WIDTH, MOBILE_HEIGHT);

        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = loginAsStandardUser();

        int count = inventoryPage.getProductCount();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count);

        loginPage.openHamburgerMenu();
        assertTrue(loginPage.isHamburgerMenuOpen(), "the menu opens on a phone as well");
        loginPage.closeHamburgerMenu();
    }

    @Test
    @DisplayName("The shop works on a tablet viewport")
    void worksOnTabletViewport() {
        setViewport(TABLET_WIDTH, TABLET_HEIGHT);

        InventoryPage inventoryPage = loginAsStandardUser();
        int count = inventoryPage.getProductCount();
        assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count);
        assertTrue(inventoryPage.isFirstProductVisible(), "the catalogue is rendered on a tablet");
    }

    @Test
    @DisplayName("The shop survives a viewport resize during interaction")
    void handlesViewportResizeDuringInteraction() {
        InventoryPage inventoryPage = loginAsStandardUser();

        // Resize to mobile.
        setViewport(MOBILE_WIDTH, MOBILE_HEIGHT);
        inventoryPage.addProductToCart(0);

        // Resize to tablet.
        setViewport(TABLET_WIDTH, TABLET_HEIGHT);
        inventoryPage.waitForCartBadgeCount(1);
    }

    @Test
    @DisplayName("Products can be added to the cart on a mobile viewport")
    void addsToCartOnMobileViewport() {
        setViewport(MOBILE_WIDTH, MOBILE_HEIGHT);

        InventoryPage inventoryPage = loginAsStandardUser();
        inventoryPage.addProductToCart(0);
        inventoryPage.addProductToCart(1);

        inventoryPage.waitForCartBadgeCount(2);
    }

    @Test
    @DisplayName("The checkout works on a tablet viewport")
    void checksOutOnTabletViewport() {
        setViewport(TABLET_WIDTH, TABLET_HEIGHT);

        InventoryPage inventoryPage = loginAsStandardUser();
        CartPage cartPage = new CartPage(driver);
        CheckoutPage checkoutPage = new CheckoutPage(driver);

        inventoryPage.addProductToCart(0);
        inventoryPage.openCart();
        cartPage.waitForPageLoad();

        cartPage.checkout();
        checkoutPage.waitForPageLoad();

        checkoutPage.fillFirstName("Test");
        checkoutPage.fillLastName("User");
        checkoutPage.fillZipCode("12345");
        checkoutPage.clickContinue();

        checkoutPage.waitForPageLoad();

        // We should be on the overview page (checkout-step-two).
        String url = driver.getCurrentUrl();
        assertTrue(url.contains("checkout-step-two"), "the overview is reached on a tablet: " + url);
    }
}