export interface LoginTestParams {
  username: string;
  password: string;
  shouldLogin: boolean;
  expectedTitle: string;
  expectedError?: string;
}

/**
 * Configuration from environment variables
 */
export const config = {
  username: process.env.SAUCE_DEMO_USERNAME ?? "standard_user",
  password: process.env.SAUCE_DEMO_PASSWORD ?? "secret_sauce",
  baseUrl: process.env.SAUCE_DEMO_BASE_URL ?? "https://qdenp.github.io/playwright-test/",
};

/**
 * Timeouts for waits
 */
export const timeouts = {
  DEFAULT: 10000,
  NAVIGATION: 30000,
  /** performance_glitch_user is deliberately slow - needs room to log in */
  SLOW: 60000,
  ERROR: 3000,
  ASSERT: 5000,
};

/** Expected number of products on the inventory page */
export const EXPECTED_PRODUCT_COUNT = 6;

/**
 * Products served by the GitHub Pages app (localStorage-based).
 * Used by fixtures and skipped tests to populate localStorage.
 */
export const PRODUCTS = [
  { id: "sauce_labs_backpack", name: "Sauce Labs Backpack", price: 29.99, description: "carry.allTheThings()", image: "images/saucelabs_backpack.jpg" },
  { id: "sauce_labs_bike_light", name: "Sauce Labs Bike Light", price: 9.99, description: "Water-resistant", image: "images/sauce_labs_bike_light.jpg" },
  { id: "sauce_labs_fleece_jacket", name: "Sauce Labs Fleece Jacket", price: 49.99, description: "Stylish fleece", image: "images/sauce_labs_fleece_jacket.jpg" },
  { id: "sauce_labs_light_bike", name: "Sauce Labs Light Bike", price: 15.99, description: "Lightweight", image: "images/sauce_labs_light_bike.jpg" },
  { id: "sauce_labs_bolt_tee", name: "Sauce Labs Bolt T-Shirt", price: 15.99, description: "Full-sleeve", image: "images/sauce_labs_bolt_tee.jpg" },
  { id: "sauce_labs_fast_bike", name: "Sauce Labs Fast Bike", price: 53.99, description: "Racing bicycle", image: "images/sauce_labs_fast_bike.jpg" },
];

/** Users whose localStorage must be pre-populated with products */
export const TEST_USERS = [
  "standard_user",
  "problem_user",
  "performance_glitch_user",
  "error_user",
  "visual_user",
  "locked_out_user",
];

/** Expected text on the order completion page */
export const COMPLETE_ORDER_TEXT = "Thank you for your order";

/**
 * Performance budgets for load-time assertions (in ms).
 * Separate from timeouts — these are business thresholds, not wait limits.
 */
export const performanceBudgets = {
  LOGIN_PAGE: 3000,
  INVENTORY_PAGE: 3000,
  PRODUCTS_LIST: 5000,
  CART_PAGE: 4000,
  AVERAGE_LOAD: 5000,
  IMAGES: 3000,
};

/**
 * Parameterized login test cases
 */
export const loginTests: LoginTestParams[] = [
  {
    username: "standard_user",
    password: "secret_sauce",
    shouldLogin: true,
    expectedTitle: "Products",
  },
  {
    username: "problem_user",
    password: "secret_sauce",
    shouldLogin: true,
    expectedTitle: "Products",
  },
  {
    username: "performance_glitch_user",
    password: "secret_sauce",
    shouldLogin: true,
    expectedTitle: "Products",
  },
  {
    username: "error_user",
    password: "secret_sauce",
    shouldLogin: true,
    expectedTitle: "Products",
  },
  {
    username: "visual_user",
    password: "secret_sauce",
    shouldLogin: true,
    expectedTitle: "Products",
  },
  {
    username: "locked_out_user",
    password: "secret_sauce",
    shouldLogin: false,
    expectedTitle: "Products",
    expectedError: "locked out",
  },
  {
    username: "standard_user",
    password: "wrong_password",
    shouldLogin: false,
    expectedTitle: "Products",
    expectedError: "Username and password do not match",
  },
  {
    username: "",
    password: "secret_sauce",
    shouldLogin: false,
    expectedTitle: "Products",
    expectedError: "Username is required",
  },
];

