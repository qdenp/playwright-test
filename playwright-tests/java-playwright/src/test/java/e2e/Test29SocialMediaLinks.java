package e2e;

import com.microsoft.playwright.*;
import pageobjects.InventoryPage;
import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.*;

import java.util.List;

/**
 * Social links at the bottom of the screen (X, Facebook, LinkedIn)
 */
class Test29SocialMediaLinks extends BaseTest {

    @Test
    void shouldShowAllThreeSocialLinksWithTheExpectedTargets(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        Assertions.assertEquals("https://x.com/saucelabs",
                inventoryPage.getSocialXLink().getAttribute("href"));
        Assertions.assertEquals("https://www.facebook.com/saucelabs",
                inventoryPage.getSocialFacebookLink().getAttribute("href"));
        Assertions.assertEquals("https://www.linkedin.com/company/sauce-labs/",
                inventoryPage.getSocialLinkedinLink().getAttribute("href"));
    }

    @Test
    void shouldOpenEachSocialLinkInANewTab(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        Assertions.assertEquals("_blank", inventoryPage.getSocialXLink().getAttribute("target"));
        Assertions.assertTrue(inventoryPage.getSocialXLink().getAttribute("rel").contains("noreferrer"));

        Assertions.assertEquals("_blank", inventoryPage.getSocialFacebookLink().getAttribute("target"));
        Assertions.assertTrue(inventoryPage.getSocialFacebookLink().getAttribute("rel").contains("noreferrer"));

        Assertions.assertEquals("_blank", inventoryPage.getSocialLinkedinLink().getAttribute("target"));
        Assertions.assertTrue(inventoryPage.getSocialLinkedinLink().getAttribute("rel").contains("noreferrer"));

        // Follow the X link and confirm it leaves the shop
        Page popup = page.waitForPopup(new Page.WaitForPopupOptions()
                .setTimeout(TestConfig.DEFAULT_TIMEOUT), () -> {
            inventoryPage.getSocialXLink().click();
        });
        Assertions.assertTrue(popup.url().matches(".*x\\.com.*"));
    }

    @Test
    void shouldKeepTheInventoryPageUsableAfterOpeningASocialLink(Page page) {
        BaseTest.AuthenticatedPage authenticatedPage = setupAuthenticatedPage(page);
        InventoryPage inventoryPage = authenticatedPage.inventoryPage;

        Page popup = page.waitForPopup(new Page.WaitForPopupOptions()
                .setTimeout(TestConfig.DEFAULT_TIMEOUT), () -> {
            inventoryPage.getSocialFacebookLink().click();
        });
        popup.close();

        // The shop is untouched and still holds the products
        Assertions.assertTrue(page.url().matches(".*inventory\\.html.*"));
        Assertions.assertEquals(6, inventoryPage.getProducts().count());
    }
}
