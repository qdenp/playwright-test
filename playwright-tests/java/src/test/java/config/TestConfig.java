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
    public static final String BASE_URL = env("SAUCE_DEMO_BASE_URL", "https://www.saucedemo.com");

    /**
     * Default explicit wait, in seconds.
     *
     * The other suites wait up to 60s for a single action; on a small machine
     * (2 CPUs) a full sequential run starves the browser and 10s turns out to
     * be too tight for cart interactions.
     */
    public static final int DEFAULT_TIMEOUT = envInt("SAUCE_DEMO_TIMEOUT", 30);

    /** Budget for navigations, in seconds. */
    public static final int NAVIGATION_TIMEOUT = envInt("SAUCE_DEMO_NAVIGATION_TIMEOUT", 30);

    /**
     * Long budget for performance_glitch_user, which logs in deliberately slowly.
     */
    public static final int SLOW_TIMEOUT = envInt("SAUCE_DEMO_SLOW_TIMEOUT", 60);

    /** Expected number of products on the inventory page. */
    public static final int EXPECTED_PRODUCT_COUNT = 6;

    /** Sales tax applied at checkout (8%). */
    public static final double CHECKOUT_TAX_RATE = 0.08;

    /** Path of the Chrome binary used by the tests. */
    public static final String CHROME_BINARY = env("CHROME_BINARY", "/usr/bin/google-chrome");

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
     */
    public static final double BUDGET_SCALE =
        Double.parseDouble(env("SAUCE_DEMO_BUDGET_SCALE", "1"));

    /** Budget in {@code milliseconds} after applying {@link #BUDGET_SCALE}. */
    public static long budget(long milliseconds) {
        return (long) (milliseconds * BUDGET_SCALE);
    }

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
