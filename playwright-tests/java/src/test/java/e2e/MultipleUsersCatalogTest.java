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
public class MultipleUsersCatalogTest extends BaseTest {

    private static final List<String[]> TEST_USERS = List.of(
        new String[] {"standard_user", "secret_sauce"},
        new String[] {"problem_user", "secret_sauce"},
        new String[] {"performance_glitch_user", "secret_sauce"},
        new String[] {"error_user", "secret_sauce"},
        new String[] {"visual_user", "secret_sauce"});

    @Test
    @DisplayName("All users see the same catalogue")
    void allUsersSeeTheSameCatalogue() {
        List<String> baselineNames = null;

        for (String[] user : TEST_USERS) {
            // A fresh browser per user: a second login inside one Chrome
            // session is swallowed by the browser cache (see BaseTest).
            restartBrowser();
            var inventoryPage = loginAs(user[0], user[1]);

            // All users see the same number of named products.
            inventoryPage.waitForProductNamesCount(TestConfig.EXPECTED_PRODUCT_COUNT);
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
