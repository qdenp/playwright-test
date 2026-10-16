package e2e;

import com.microsoft.playwright.*;
import config.TestConfig;
import pageobjects.InventoryPage;
import pageobjects.LoginPage;
import org.junit.jupiter.api.*;

import java.util.List;
import base.BaseTest;

/**
 * All users see the same catalogue
 */
class Test10MultipleUsersCatalog extends BaseTest {

    @Test
    void allUsersSeeTheSameCatalogue(Page page) {
        LoginPage loginPage = new LoginPage(page);
        InventoryPage inventoryPage = new InventoryPage(page);

        // Take the first 5 users
        String[][] testUsers = {
                {"standard_user", "secret_sauce"},
                {"problem_user", "secret_sauce"},
                {"performance_glitch_user", "secret_sauce"},
                {"error_user", "secret_sauce"},
                {"visual_user", "secret_sauce"}
        };

        List<String> baselineNames = null;

        for (String[] user : testUsers) {
            loginPage.goTo();
            loginPage.login(user[0], user[1]);
            inventoryPage.waitForPageLoad();

            // All users see the same number of named products
            Assertions.assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, inventoryPage.getProductNames().count());

            List<String> names = inventoryPage.getAllProductNames();

            if (baselineNames == null) {
                baselineNames = names;
            } else {
                Assertions.assertEquals(baselineNames, names);
            }
        }
    }
}
