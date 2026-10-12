export interface User {
  username: string;
  password: string;
}

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
 * Таймауты для ожиданий
 */
export const timeouts = {
  DEFAULT: 10000,
  NAVIGATION: 30000,
  ERROR: 3000,
  ASSERT: 5000,
};

/** Ожидаемое число товаров на странице каталога */
export const EXPECTED_PRODUCT_COUNT = 6;

/**
 * All available users
 */
export const users: User[] = [
  { username: "standard_user", password: "secret_sauce" },
  { username: "problem_user", password: "secret_sauce" },
  { username: "performance_glitch_user", password: "secret_sauce" },
  { username: "error_user", password: "secret_sauce" },
  { username: "visual_user", password: "secret_sauce" },
  { username: "locked_out_user", password: "secret_sauce" },
];

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
];

/**
 * Get all credentials from environment or defaults
 */
export function getAllCredentials(): Map<string, string> {
  const credentials = new Map<string, string>();
  for (const user of users) {
    credentials.set(user.username, user.password);
  }
  return credentials;
}

/**
 * Get login parameters for parameterized test
 */
export function getLoginParams(index: number): LoginTestParams {
  return loginTests[index];
}
