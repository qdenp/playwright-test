package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;
import base.BaseTest;
import org.junit.jupiter.api.*;

/**
 * Successful login with the standard user
 */
class Test01Login extends BaseTest {

    @Test
    void successfulLoginWithTheStandardUser(Page page) {
        LoginPage loginPage = new LoginPage(page);
        InventoryPage inventoryPage = new InventoryPage(page);

        loginPage.goTo();
        loginPage.login();
        inventoryPage.waitForPageLoad();

        // Verify we're on the products page
        Assertions.assertEquals("Products", inventoryPage.getTitleText());
        Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProductCount());
    }
}
