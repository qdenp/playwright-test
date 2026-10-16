package config;

import java.util.List;
import java.util.Map;

/**
 * Configuration constants mirroring TypeScript tests/config.ts
 */
public final class TestConfig {

    private TestConfig() {}

    // --- Environment ---
    public static final String USERNAME = System.getenv("SAUCE_DEMO_USERNAME") != null
            ? System.getenv("SAUCE_DEMO_USERNAME") : "standard_user";
    public static final String PASSWORD = System.getenv("SAUCE_DEMO_PASSWORD") != null
            ? System.getenv("SAUCE_DEMO_PASSWORD") : "secret_sauce";
    public static final String BASE_URL = System.getenv("SAUCE_DEMO_BASE_URL") != null
            ? System.getenv("SAUCE_DEMO_BASE_URL") : "https://qdenp.github.io/playwright-test/";

    // --- Timeouts ---
    public static final long DEFAULT_TIMEOUT = 10000;
    public static final long NAVIGATION_TIMEOUT = 30000;
    public static final long SLOW_TIMEOUT = 60000;
    public static final long ERROR_TIMEOUT = 3000;
    public static final long ASSERT_TIMEOUT = 5000;

    // --- Constants ---
    public static final int EXPECTED_PRODUCT_COUNT = 6;
    public static final String COMPLETE_ORDER_TEXT = "Thank you for your order";

    // --- Performance budgets ---
    public static final int LOGIN_PAGE_BUDGET = 3000;
    public static final int INVENTORY_PAGE_BUDGET = 3000;
    public static final int PRODUCTS_LIST_BUDGET = 5000;
    public static final int CART_PAGE_BUDGET = 4000;
    public static final int AVERAGE_LOAD_BUDGET = 5000;
    public static final int IMAGES_BUDGET = 3000;

    // --- Products (localStorage-based) ---
    public static final String[] PRODUCT_IDS = {
            "sauce_labs_backpack",
            "sauce_labs_bike_light",
            "sauce_labs_fleece_jacket",
            "sauce_labs_light_bike",
            "sauce_labs_bolt_tee",
            "sauce_labs_fast_bike"
    };

    public static final String[] PRODUCT_NAMES = {
            "Sauce Labs Backpack",
            "Sauce Labs Bike Light",
            "Sauce Labs Fleece Jacket",
            "Sauce Labs Light Bike",
            "Sauce Labs Bolt T-Shirt",
            "Sauce Labs Fast Bike"
    };

    public static final double[] PRODUCT_PRICES = {
            29.99, 9.99, 49.99, 15.99, 15.99, 53.99
    };

    public static final String[] PRODUCT_DESCRIPTIONS = {
            "carry.allTheThings()",
            "Water-resistant",
            "Stylish fleece",
            "Lightweight",
            "Full-sleeve",
            "Racing bicycle"
    };

    // --- Test users ---
    public static final String[] TEST_USERS = {
            "standard_user",
            "problem_user",
            "performance_glitch_user",
            "error_user",
            "visual_user",
            "locked_out_user"
    };

    // --- Login test cases ---
    public static final Map<String, Object>[] LOGIN_TEST_CASES = new Map[]{
            Map.of("username", "standard_user", "password", "secret_sauce", "shouldLogin", true,
                    "expectedTitle", "Products", "expectedError", ""),
            Map.of("username", "problem_user", "password", "secret_sauce", "shouldLogin", true,
                    "expectedTitle", "Products", "expectedError", ""),
            Map.of("username", "performance_glitch_user", "password", "secret_sauce", "shouldLogin", true,
                    "expectedTitle", "Products", "expectedError", ""),
            Map.of("username", "error_user", "password", "secret_sauce", "shouldLogin", true,
                    "expectedTitle", "Products", "expectedError", ""),
            Map.of("username", "visual_user", "password", "secret_sauce", "shouldLogin", true,
                    "expectedTitle", "Products", "expectedError", ""),
            Map.of("username", "locked_out_user", "password", "secret_sauce", "shouldLogin", false,
                    "expectedTitle", "Products", "expectedError", "locked out"),
            Map.of("username", "standard_user", "password", "wrong_password", "shouldLogin", false,
                    "expectedTitle", "Products", "expectedError", "Username and password do not match"),
            Map.of("username", "", "password", "secret_sauce", "shouldLogin", false,
                    "expectedTitle", "Products", "expectedError", "Username is required")
    };

    // --- Product names helper ---
    public static String productName(int index) {
        return PRODUCT_NAMES[index];
    }
}
