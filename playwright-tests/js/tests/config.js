/**
 * Конфигурация для тестов
 */
export const config = {
  username: process.env.SAUCE_DEMO_USERNAME || 'standard_user',
  password: process.env.SAUCE_DEMO_PASSWORD || 'secret_sauce',
  baseUrl: process.env.SAUCE_DEMO_BASE_URL || 'https://www.saucedemo.com',
};

/**
 * Все валидные пользователи и их пароли
 */
export const users = [
  { username: 'standard_user', password: process.env.SAUCE_DEMO_PASSWORD_STANDARD || 'secret_sauce' },
  { username: 'problem_user', password: process.env.SAUCE_DEMO_PASSWORD_PROBLEM || 'secret_sauce' },
  { username: 'performance_glitch_user', password: process.env.SAUCE_DEMO_PASSWORD_PERFORMANCE || 'secret_sauce' },
  { username: 'error_user', password: process.env.SAUCE_DEMO_PASSWORD_ERROR || 'secret_sauce' },
  { username: 'visual_user', password: process.env.SAUCE_DEMO_PASSWORD_VISUAL || 'secret_sauce' },
  { username: 'locked_out_user', password: process.env.SAUCE_DEMO_PASSWORD_LOCKED || 'secret_sauce' },
];

/**
 * Параметры для параметризованного теста
 */
export const loginTests = [
  { username: 'standard_user', password: 'secret_sauce', shouldLogin: true, expectedTitle: 'Products', expectedError: null },
  { username: 'problem_user', password: 'secret_sauce', shouldLogin: true, expectedTitle: 'Products', expectedError: null },
  { username: 'performance_glitch_user', password: 'secret_sauce', shouldLogin: true, expectedTitle: 'Products', expectedError: null },
  { username: 'error_user', password: 'secret_sauce', shouldLogin: true, expectedTitle: 'Products', expectedError: null },
  { username: 'visual_user', password: 'secret_sauce', shouldLogin: true, expectedTitle: 'Products', expectedError: null },
  { username: 'locked_out_user', password: 'secret_sauce', shouldLogin: false, expectedTitle: null, expectedError: 'locked out' },
  { username: 'standard_user', password: 'wrong_password', shouldLogin: false, expectedTitle: null, expectedError: 'do not match' },
  { username: '', password: 'secret_sauce', shouldLogin: true, expectedTitle: 'Products', expectedError: null },
];
