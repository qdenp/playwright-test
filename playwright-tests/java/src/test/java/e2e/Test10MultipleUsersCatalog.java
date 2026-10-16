package e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;

import base.BaseTest;
import config.TestConfig;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * All users see the same catalogue.
 */
public class Test10MultipleUsersCatalog extends BaseTest {

    @Test
    @DisplayName("All users see the same catalogue")
    void allUsersSeeTheSameCatalogue() {
        List<String> baselineNames = null;

        // Test all users except locked_out_user (who cannot log in).
        for (String[] user : TestConfig.USERS) {
            if (user[0].equals(TestConfig.LOCKED_OUT_USER)) {
                continue;
            }
            // A fresh browser per user: a second login inside one Chrome
            // session is swallowed by the browser cache (see BaseTest).
            restartBrowser();
            var inventoryPage = loginAs(user[0], user[1]);

            // All users see the same number of named products.
            int count = inventoryPage.getProductCount();
            assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, count,
                "user " + user[0] + " sees the whole catalogue");

            List<String> names = inventoryPage.getAllProductNames();
            assertEquals(TestConfig.EXPECTED_PRODUCT_COUNT, names.size(),
                "user " + user[0] + " sees the whole catalogue");

            if (baselineNames == null) {
                baselineNames = names;
            } else {
                assertEquals(baselineNames, names, "user " + user[0] + " sees the same products");
            }
        }
    }
}