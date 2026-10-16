package config;

/**
 * Central configuration for the SauceDemo test suite.
 *
 * Credentials and the base URL are read from the environment, exactly like the
 * TypeScript / JavaScript / Python reference suites:
 * <pre>
 *   SAUCE_DEMO_USERNAME   standard_user            (login used by most tests)
 *   SAUCE_DEMO_PASSWORD   secret_sauce
 *   SAUCE_DEMO_BASE_URL   https://www.saucedemo.com
 * </pre>
 * No real secret is stored in the repository: the defaults belong to the public
 * SauceDemo demo stand.
 */
public final class TestConfig {

    /** Username used by the tests that need an authenticated session. */
    public static final String USERNAME = env("SAUCE_DEMO_USERNAME", "standard_user");

    /** Password of {@link #USERNAME}. */
    public static final String PASSWORD = env("SAUCE_DEMO_PASSWORD", "secret_sauce");

    /** Root URL of the application under test. */
    public static final String BASE_URL = env("SAUCE_DEMO_BASE_URL", "https://qdenp.github.io/playwright-test/");

    /**
     * Default explicit wait, in seconds.
     *
     * The other suites wait up to 60s for a single action; on a small machine
     * (2 CPUs) a full sequential run starves the browser and 10s turns out to
     * be too tight for cart interactions.
     */
    public static final int DEFAULT_TIMEOUT = envInt("SAUCE_DEMO_TIMEOUT", 90);

    /** Budget for navigations, in seconds. */
    public static final int NAVIGATION_TIMEOUT = envInt("SAUCE_DEMO_NAVIGATION_TIMEOUT", 90);

    /**
     * Long budget for performance_glitch_user, which logs in deliberately slowly.
     */
    public static final int SLOW_TIMEOUT = envInt("SAUCE_DEMO_SLOW_TIMEOUT", 180);

    /** Expected number of products on the inventory page. */
    public static final int EXPECTED_PRODUCT_COUNT = 6;

    /** Sales tax applied at checkout (8%). */
    public static final double CHECKOUT_TAX_RATE = 0.08;

    /** Path of the Chrome binary used by the tests. */
    public static final String CHROME_BINARY =
        env("CHROME_BINARY",
            System.getProperty("os.name").toLowerCase().contains("win")
                ? "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe"
                : "");

    /** Test data used by the checkout scenarios. */
    public static final String CHECKOUT_FIRST_NAME = "Ivan";
    public static final String CHECKOUT_LAST_NAME = "Ivanov";
    public static final String CHECKOUT_POSTAL_CODE = "100000";

    /**
     * Multiplier for the performance budgets of the performance scenario.
     *
     * The budgets themselves mirror the reference suites and assume a normal
     * developer machine. A starved box (e.g. a 2-CPU container shared with an
     * agent runtime) measures multi-second page loads that have nothing to do
     * with the application; scale the budgets up there instead of moving the
     * goalposts for everybody.
     *
     * <p>Selenium WebDriver carries more protocol overhead than Playwright, so
     * the default is 2× — still tight enough to catch regressions.</p>
     */
    public static final double BUDGET_SCALE =
        Double.parseDouble(env("SAUCE_DEMO_BUDGET_SCALE", "2"));

    /** Budget in {@code milliseconds} after applying {@link #BUDGET_SCALE}. */
    public static long budget(long milliseconds) {
        return (long) (milliseconds * BUDGET_SCALE);
    }

    /**
     * Products on the inventory page in the default A-Z sort order.
     *
     * <p>The application under test (saucedemo.com, mirrored on GitHub Pages)
     * ships exactly six products and sorts them by name ascending by default.</p>
     */
    public static final String[][] PRODUCTS = {
        {"sauce_labs_backpack", "Sauce Labs Backpack", "29.99"},
        {"sauce_labs_bike_light", "Sauce Labs Bike Light", "9.99"},
        {"sauce_labs_bolt_tee", "Sauce Labs Bolt T-Shirt", "15.99"},
        {"sauce_labs_fleece_jacket", "Sauce Labs Fleece Jacket", "49.99"},
        {"sauce_labs_onesie", "Sauce Labs Onesie", "7.99"},
        {"sauce_labs_test_all_the_things_tee", "Test.allTheThings() T-Shirt (Red)", "15.99"},
    };

    /** Product names in catalogue A-Z order. */
    public static String productName(int index) {
        return PRODUCTS[index][1];
    }

    /** Username for the locked-out user (always rejected at login). */
    public static final String LOCKED_OUT_USER = "locked_out_user";

    /** Password fragment for malformed-credential test cases. */
    public static final String WRONG_PASSWORD = "wrong_password";

    /** Title text on the inventory page after a successful login. */
    public static final String INVENTORY_PAGE_TITLE = "Products";

    /** Relative path of the inventory page (without leading slash). */
    public static final String INVENTORY_PAGE_PATH = "inventory.html";

    /** Relative path of the cart page (without leading slash). */
    public static final String CART_PAGE_PATH = "cart.html";

    /** Sub-string that appears in the password-mismatch error message. */
    public static final String ERROR_PASSWORD_MISMATCH = "Username and password do not match";

    /** Sub-string that appears in the locked-out error message. */
    public static final String ERROR_LOCKED_OUT = "locked out";

    /** Confirmation header shown on the order-complete page. */
    public static final String COMPLETE_ORDER_TEXT = "Thank you for your order";

    /** Tolerance (in dollars) for floating-point comparisons of tax totals. */
    public static final double TAX_TOLERANCE = 0.01;

    /**
     * Parameterized login test cases, mirroring {@code loginTests} in the
     * TypeScript reference config. Each entry is a 5-element array:
     * {@code {username, password, shouldLogin, expectedTitle, expectedError}}.
     */
    public static final Object[][] LOGIN_TEST_CASES = {
        {"standard_user", "secret_sauce", true, INVENTORY_PAGE_TITLE, null},
        {"problem_user", "secret_sauce", true, INVENTORY_PAGE_TITLE, null},
        {"performance_glitch_user", "secret_sauce", true, INVENTORY_PAGE_TITLE, null},
        {"error_user", "secret_sauce", true, INVENTORY_PAGE_TITLE, null},
        {"visual_user", "secret_sauce", true, INVENTORY_PAGE_TITLE, null},
        {LOCKED_OUT_USER, "secret_sauce", false, INVENTORY_PAGE_TITLE, ERROR_LOCKED_OUT},
        {"standard_user", WRONG_PASSWORD, false, INVENTORY_PAGE_TITLE, ERROR_PASSWORD_MISMATCH},
        {"", "secret_sauce", false, INVENTORY_PAGE_TITLE, "Username is required"},
    };

    /** Performance budgets (in milliseconds) mirroring the TS reference suite. */
    public static final long LOGIN_PAGE_BUDGET = 3000;
    public static final long INVENTORY_BUDGET = 3000;
    public static final long CATALOGUE_BUDGET = 5000;
    public static final long CART_BUDGET = 4000;

    /** All users of the demo stand, with their password. */
    public static final String[][] USERS = {
        {"standard_user", "secret_sauce"},
        {"problem_user", "secret_sauce"},
        {"performance_glitch_user", "secret_sauce"},
        {"error_user", "secret_sauce"},
        {"visual_user", "secret_sauce"},
        {"locked_out_user", "secret_sauce"},
    };

    private TestConfig() {
    }

    /** Password of a known user of the demo stand. */
    public static String passwordOf(String username) {
        for (String[] user : USERS) {
            if (user[0].equals(username)) {
                return user[1];
            }
        }
        throw new IllegalArgumentException("Unknown user: " + username);
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static int envInt(String name, int fallback) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
