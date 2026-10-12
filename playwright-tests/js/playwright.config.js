import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests',

  // Запускать тесты параллельно
  fullyParallel: true,

  // Не повторять упавшие тесты — только на CI
  retries: process.env.CI ? 2 : 0,

  // Количество воркеров
  workers: process.env.CI ? 1 : 3,

  // Timeout на каждый тест: ТОЛЬКО на верхнем уровне.
  // Внутри `use` этот ключ молча игнорируется и применяется дефолт 30s.
  timeout: 120000,

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
    actionTimeout: 60000,
    navigationTimeout: 30000,
  },

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
  ],
});
