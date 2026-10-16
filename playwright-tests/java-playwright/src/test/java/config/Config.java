package config;

/**
 * Configuration and constants for SauceDemo tests.
 * Mirrors TypeScript tests/config.ts
 */
public final class Config {

    private Config() {}

    // ── Credentials ──────────────────────────────────────────────

    public static final String USERNAME = System.getenv("SAUCE_DEMO_USERNAME") != null
            ? System.getenv("SAUCE_DEMO_USERNAME") : "standard_user";
    public static final String PASSWORD = System.getenv("SAUCE_DEMO_PASSWORD") != null
            ? System.getenv("SAUCE_DEMO_PASSWORD") : "secret_sauce";
    public static final String BASE_URL = System.getenv("SAUCE_DEMO_BASE_URL") != null
            ? System.getenv("SAUCE_DEMO_BASE_URL") : "https://qdenp.github.io/playwright-test/";

    // ── Timeouts (ms) ───────────────────────────────────────────

    public static final long DEFAULT_TIMEOUT = 10000;
    public static final long NAVIGATION_TIMEOUT = 30000;
    public static final long SLOW_TIMEOUT = 60000;
    public static final long ERROR_TIMEOUT = 3000;
    public static final long ASSERT_TIMEOUT = 5000;

    // ── Constants ───────────────────────────────────────────────

    public static final int EXPECTED_PRODUCT_COUNT = 6;
    public static final String COMPLETE_ORDER_TEXT = "Thank you for your order";

    // ── Products ────────────────────────────────────────────────

    public record Product(String id, String name, double price, String description, String image) {}

    public static final Product[] PRODUCTS = {
            new Product("sauce_labs_backpack", "Sauce Labs Backpack", 29.99, "carry.allTheThings()", "images/saucelabs_backpack.jpg"),
            new Product("sauce_labs_bike_light", "Sauce Labs Bike Light", 9.99, "Water-resistant", "images/sauce_labs_bike_light.jpg"),
            new Product("sauce_labs_fleece_jacket", "Sauce Labs Fleece Jacket", 49.99, "Stylish fleece", "images/sauce_labs_fleece_jacket.jpg"),
            new Product("sauce_labs_light_bike", "Sauce Labs Light Bike", 15.99, "Lightweight", "images/sauce_labs_light_bike.jpg"),
            new Product("sauce_labs_bolt_tee", "Sauce Labs Bolt T-Shirt", 15.99, "Full-sleeve", "images/sauce_labs_bolt_tee.jpg"),
            new Product("sauce_labs_fast_bike", "Sauce Labs Fast Bike", 53.99, "Racing bicycle", "images/sauce_labs_fast_bike.jpg"),
    };

    // ── Users ───────────────────────────────────────────────────

    public static final String[] TEST_USERS = {
            "standard_user",
            "problem_user",
            "performance_glitch_user",
            "error_user",
            "visual_user",
            "locked_out_user",
    };

    // ── Performance budgets (ms) ────────────────────────────────

    public static final long LOGIN_PAGE_BUDGET = 3000;
    public static final long INVENTORY_PAGE_BUDGET = 3000;
    public static final long PRODUCTS_LIST_BUDGET = 5000;
    public static final long CART_PAGE_BUDGET = 4000;
    public static final long AVERAGE_LOAD_BUDGET = 5000;
    public static final long IMAGES_BUDGET = 3000;

    // ── Login test cases ────────────────────────────────────────

    public record LoginTestCase(String username, String password, boolean shouldLogin, String expectedTitle, String expectedError) {}

    public static final LoginTestCase[] LOGIN_TEST_CASES = {
            new LoginTestCase("standard_user", "secret_sauce", true, "Products", null),
            new LoginTestCase("problem_user", "secret_sauce", true, "Products", null),
            new LoginTestCase("performance_glitch_user", "secret_sauce", true, "Products", null),
            new LoginTestCase("error_user", "secret_sauce", true, "Products", null),
            new LoginTestCase("visual_user", "secret_sauce", true, "Products", null),
            new LoginTestCase("locked_out_user", "secret_sauce", false, "Products", "locked out"),
            new LoginTestCase("standard_user", "wrong_password", false, "Products", "Username and password do not match"),
            new LoginTestCase("", "secret_sauce", false, "Products", "Username is required"),
    };
}
