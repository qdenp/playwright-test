package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pageobjects.InventoryPage;

/**
 * Social links at the bottom of the screen (X, Facebook, LinkedIn).
 */
public class SocialMediaLinksTest extends BaseTest {

    private static final String X_URL = "https://x.com/saucelabs";
    private static final String FACEBOOK_URL = "https://www.facebook.com/saucelabs";
    private static final String LINKEDIN_URL = "https://www.linkedin.com/company/sauce-labs/";

    @Test
    @DisplayName("All three social links point at the expected targets")
    void socialLinksHaveTheExpectedTargets() {
        InventoryPage inventoryPage = loginAsStandardUser();

        assertEquals(X_URL, inventoryPage.getSocialXHref());
        assertEquals(FACEBOOK_URL, inventoryPage.getSocialFacebookHref());
        assertEquals(LINKEDIN_URL, inventoryPage.getSocialLinkedinHref());
    }

    @Test
    @DisplayName("Every social link opens in a new tab")
    void socialLinksOpenInANewTab() {
        InventoryPage inventoryPage = loginAsStandardUser();

        assertEquals("_blank", inventoryPage.getSocialXTarget());
        assertEquals("_blank", inventoryPage.getSocialFacebookTarget());
        assertEquals("_blank", inventoryPage.getSocialLinkedinTarget());
        assertTrue(inventoryPage.getSocialXRel().contains("noreferrer"));
        assertTrue(inventoryPage.getSocialFacebookRel().contains("noreferrer"));
        assertTrue(inventoryPage.getSocialLinkedinRel().contains("noreferrer"));

        // Follow the X link and confirm it leaves the shop.
        String shopWindow = driver.getWindowHandle();
        inventoryPage.clickSocialXLink();

        waitForWindowCount(2);
        switchToOtherWindow(shopWindow);

        String popupUrl = waitFor(TestConfig.DEFAULT_TIMEOUT).until(d -> {
            String url = d.getCurrentUrl();
            return url.contains("x.com") ? url : null;
        });
        assertNotNull(popupUrl, "the X link opens " + X_URL);
    }

    @Test
    @DisplayName("The shop stays usable after opening a social link")
    void shopStaysUsableAfterOpeningASocialLink() {
        InventoryPage inventoryPage = loginAsStandardUser();

        String shopWindow = driver.getWindowHandle();
        inventoryPage.clickSocialFacebookLink();

        waitForWindowCount(2);
        switchToOtherWindow(shopWindow);
        driver.close();
        driver.switchTo().window(shopWindow);

        // The shop is untouched and still holds the products.
        assertTrue(driver.getCurrentUrl().contains("inventory.html"), "the shop is still open");
        inventoryPage.waitForProductCount(TestConfig.EXPECTED_PRODUCT_COUNT);
    }
}
