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
  baseUrl: process.env.SAUCE_DEMO_BASE_URL ?? "https://www.saucedemo.com",
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

