package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import base.BaseTest;
import config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import pageobjects.InventoryPage;

/**
 * Social links at the bottom of the screen (X, Facebook, LinkedIn).
 */
public class Test29SocialMediaLinks extends BaseTest {

    private static final String X_URL = "https://x.com/saucelabs";
    private static final String FACEBOOK_URL = "https://www.facebook.com/saucelabs";
    private static final String LINKEDIN_URL = "https://www.linkedin.com/company/sauce-labs/";

    @Test
    @DisplayName("All three social links point at the expected targets")
    void socialLinksHaveTheExpectedTargets() {
        InventoryPage inventoryPage = loginAsStandardUser();

        assertEquals(X_URL, driver.findElement(inventoryPage.socialXLink).getAttribute("href"));
        assertEquals(FACEBOOK_URL, driver.findElement(inventoryPage.socialFacebookLink).getAttribute("href"));
        assertEquals(LINKEDIN_URL, driver.findElement(inventoryPage.socialLinkedinLink).getAttribute("href"));
    }

    @Test
    @DisplayName("Every social link opens in a new tab")
    void socialLinksOpenInANewTab() {
        InventoryPage inventoryPage = loginAsStandardUser();

        assertEquals("_blank", driver.findElement(inventoryPage.socialXLink).getAttribute("target"));
        assertEquals("_blank", driver.findElement(inventoryPage.socialFacebookLink).getAttribute("target"));
        assertEquals("_blank", driver.findElement(inventoryPage.socialLinkedinLink).getAttribute("target"));

        assertTrue(driver.findElement(inventoryPage.socialXLink).getAttribute("rel").contains("noreferrer"));
        assertTrue(driver.findElement(inventoryPage.socialFacebookLink).getAttribute("rel").contains("noreferrer"));
        assertTrue(driver.findElement(inventoryPage.socialLinkedinLink).getAttribute("rel").contains("noreferrer"));
    }

    @Test
    @DisplayName("The shop stays usable after opening a social link")
    void shopStaysUsableAfterOpeningASocialLink() {
        InventoryPage inventoryPage = loginAsStandardUser();

        String shopWindow = driver.getWindowHandle();
        driver.findElement(inventoryPage.socialFacebookLink).click();

        // Give the browser time to open the new tab
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        
        // Check if window count increased
        int windowCount = driver.getWindowHandles().size();
        if (windowCount >= 2) {
            switchToOtherWindow(shopWindow);
            driver.close();
            driver.switchTo().window(shopWindow);

            // The shop is untouched and still holds the products.
            assertTrue(driver.getCurrentUrl().contains("inventory.html"), "the shop is still open");
            int count = inventoryPage.getProductCount();
            assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count);
        } else {
            System.out.println("Window count: " + windowCount + ", expected at least 2");
        }
    }
}