import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests',

  // Запускать тесты параллельно
  fullyParallel: true,

  // Не повторять упавшие тесты
  forbidOnly: !!process.env.CI,

  // Повторить упавшие тесты до 3 раз
  retries: 3,

  // Количество воркеров
  workers: 3,

  // Отчёты
  reporter: [
    ['html', { open: 'never' }],
    ['list']
  ],

  // Глобальные настройки
  use: {
    baseURL: 'https://www.saucedemo.com',
    screenshot: 'only-on-failure',
    trace: 'on-first-retry',
    video: 'off',
    actionTimeout: 10000,
    navigationTimeout: 15000,
    // Visual regression
    screenshot: 'only-on-failure',
  },

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
      testIgnoreTimeout: 60000,
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
  ],
});
