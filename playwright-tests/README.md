# Playwright E2E Tests — Sauce Demo

Комплексные E2E-тесты для [Sauce Demo](https://www.saucedemo.com/) на **TypeScript**, **JavaScript**, **Python** (Playwright), **C#** (Playwright for .NET) и **Java** (Selenium).

## 📋 Содержание

| # | Описание | TS | JS | Python | C# | Java |
|---|----------|----|----|--------|----|------|
| 01 | Успешный логин | ✅ | ✅ | ✅ | ✅ | ✅ |
| 02 | Неверный пароль | ✅ | ✅ | ✅ | ✅ | ✅ |
| 03 | Заблокированный пользователь | ✅ | ✅ | ✅ | ✅ | ✅ |
| 04 | Параметризованный логин | ✅ | ✅ | ✅ | ✅ | ✅ |
| 05 | Выход из аккаунта | ✅ | ✅ | ✅ | ✅ | ✅ |
| 06 | Выход с разных страниц | ✅ | ✅ | ✅ | ✅ | ✅ |
| 07 | Сортировка по цене | ✅ | ✅ | ✅ | ✅ | ✅ |
| 08 | Сортировка по имени | ✅ | ✅ | ✅ | ✅ | ✅ |
| 09 | Страница товара | ✅ | ✅ | ✅ | ✅ | ✅ |
| 10 | Разные пользователи = одинаковый каталог | ✅ | ✅ | ✅ | ✅ | ✅ |
| 11 | Изображения товаров | ✅ | ✅ | ✅ | ✅ | ✅ |
| 12 | Карточки товаров | ✅ | ✅ | ✅ | ✅ | ✅ |
| 13 | Добавление товара в корзину | ✅ | ✅ | ✅ | ✅ | ✅ |
| 14 | Удаление из корзины | ✅ | ✅ | ✅ | ✅ | ✅ |
| 15 | Арифметика корзины (badge) | ✅ | ✅ | ✅ | ✅ | ✅ |
| 16 | Добавление/удаление всех товаров | ✅ | ✅ | ✅ | ✅ | ✅ |
| 17 | Количество в корзине не редактируется | ✅ | ✅ | ✅ | ✅ | ✅ |
| 18 | Комбинации товаров (cheapest/most expensive) | ✅ | ✅ | ✅ | ✅ | ✅ |
| 19 | Добавление всех шести товаров | ✅ | ✅ | ✅ | ✅ | ✅ |
| 20 | Полный флоу покупки | ✅ | ✅ | ✅ | ✅ | ✅ |
| 21 | Валидация чекаута | ✅ | ✅ | ✅ | ✅ | ✅ |
| 22 | Экспорт PDF заказа | ✅ | ✅ | ✅ | ✅ | ✅ |
| 23 | Гамбургер-меню | ✅ | ✅ | ✅ | ✅ | ✅ |
| 24 | About page | skip | skip | skip | skip | skip |
| 25 | Адаптивность | ✅ | ✅ | ✅ | ✅ | ✅ |
| 26 | Навигация браузера | ✅ | ✅ | ✅ | ✅ | ✅ |
| 27 | Бюджет производительности | ✅ | ✅ | ✅ | ✅ | ✅ |
| 28 | Сброс состояния приложения | ✅ | ✅ | ✅ | ✅ | ✅ |
| 29 | Ссылки на соцсети | ✅ | ✅ | ✅ | ✅ | ✅ |

`24-about-page` помечен `skip` во всех реализациях: About ведёт на
`saucelabs.com`, который отдаёт 403.

## 🚀 Запуск

### JavaScript

```bash
cd js
npm install
npx playwright test
npx playwright test --project=chromium
npx playwright test --project=firefox
npx playwright test --project=webkit
```

### Python

```bash
cd python
pip install -r requirements.txt
pytest tests/e2e/
pytest tests/e2e/ --browser chromium
pytest tests/e2e/ --browser firefox
pytest tests/e2e/ --browser webkit
```

### C# (Playwright for .NET + xUnit)

```bash
cd csharp/PlaywrightTests
dotnet playwright install chromium        # один раз на машину
dotnet test
dotnet test --filter "FullyQualifiedName~SortByPriceTests"
```

### Java (Selenium + JUnit 5 + Maven)

```bash
cd java
mvn test
mvn test -Dtest=SortByPriceTest
```

## 📊 Архитектура

```
playwright-tests/
├── typescript/             # Эталонный набор (TypeScript)
│   ├── playwright.config.ts    # Конфиг Playwright (retries, browsers)
│   ├── tests/
│   │   ├── config.ts          # Таймауты и параметры логина
│   │   ├── fixtures.ts        # Фикстура authenticatedPage
│   │   ├── e2e/               # 29 тестовых файлов (01-29)
│   │   └── page-objects/      # LoginPage, InventoryPage, CartPage, CheckoutPage
├── js/                      # Дубликат набора на JavaScript
│   ├── playwright.config.js
│   ├── tests/
│   │   ├── config.js
│   │   ├── fixtures.js
│   │   ├── e2e/               # 29 тестовых файлов (01-29)
│   │   └── page-objects/      # LoginPage, InventoryPage, CartPage, CheckoutPage
├── python/                  # Набор на Python (pytest + pytest-playwright)
│   ├── pytest.ini
│   ├── requirements.txt
│   └── tests/
│       ├── conftest.py        # Фикстуры и параметры логина
│       ├── constants.py       # Таймауты
│       ├── e2e/               # 29 тестовых файлов (01-29)
│       └── page_objects/      # login_page, inventory_page, cart_page, checkout_page
├── csharp/                  # Набор на C# (Playwright for .NET + xUnit)
│   ├── README.md                # Стек, запуск, соответствие сценариев
│   └── PlaywrightTests/
│       ├── Config/              # Таймауты, креди, бюджеты производительности
│       ├── Fixtures/            # Общий chromium, контекст+страница на тест
│       ├── Infrastructure/      # E2eTestBase (логин-хелперы, assert-хелперы)
│       ├── PageObjects/         # LoginPage, InventoryPage, CartPage, CheckoutPage
│       └── Tests/E2e/           # 29 классов сценариев (номер — в Trait)
├── java/                    # Набор на Java (Selenium + JUnit 5 + Maven)
│   ├── README.md                # Стек, запуск, соответствие сценариев
│   └── src/test/java/
│       ├── config/              # TestConfig (креды, таймауты из env)
│       ├── base/                # BaseTest (headless Chrome, хелперы)
│       ├── pageobjects/         # LoginPage, InventoryPage, CartPage, CheckoutPage
│       └── e2e/                 # 29 классов сценариев
├── README.md
├── ENV_CONFIG.md             # Документация по env vars
└── TEST_REVIEW_2026-10-12.md # Разбор качества тестов (5 заходов)
```

## 🔐 Environment Variables

| Переменная | Описание | По умолчанию |
|------------|----------|--------------|
| `SAUCE_DEMO_USERNAME` | Логин пользователя | `standard_user` |
| `SAUCE_DEMO_PASSWORD` | Пароль пользователя | `secret_sauce` |
| `SAUCE_DEMO_PASSWORDS` | Все пользователи: `user1:pass1,user2:pass2` | — |
| `SAUCE_DEMO_BASE_URL` | URL приложения | `https://www.saucedemo.com` |

### Пример `.env`

```env
SAUCE_DEMO_USERNAME=standard_user
SAUCE_DEMO_PASSWORD=secret_sauce
SAUCE_DEMO_PASSWORDS=standard_user:secret_sauce,problem_user:secret_sauce,locked_out_user:secret_sauce
SAUCE_DEMO_BASE_URL=https://www.saucedemo.com
```

## ✨ Особенности

- **Page Object Pattern** — чистая архитектура с переиспользуемыми компонентами
- **Auto-retry** — JS: `retries: 3`, Python: `--reruns 3 --reruns-delay 1`
- **Modern assertions** — Playwright assertions с auto-wait
- **Cross-browser** — Chromium, Firefox, WebKit
- **Credentials in env** — все учётные данные вынесены в переменные окружения
- **Sequential numbering** — тесты пронумерованы 01–29 и сгруппированы по функционалу:
  логин (01–06), каталог (07–12), корзина (13–19), чекаут (20–22), навигация (23–26),
  прочее (27–29)

## 📈 Результаты

Прогон на Chromium (октябрь 2026, локально):

| Реализация | Собрано | Passed | Skipped |
|---|---|---|---|
| TypeScript | 58 | 56 | 2 (`24-about-page`, `28-reset-app-state:2`) |
| JavaScript | 58 | 56 | 2 (`24-about-page`, `28-reset-app-state:2`) |
| Python | 62 | 59 | 3 (`14-remove-from-cart`, `24-about-page`, `28-reset-app-state:2`) |
| C# | 58 | 56 | 2 (`24-about-page`, `28-reset-app-state:2`) |
| Java | 58 | 58 | 2 (`24-about-page`, `28-reset-app-state:2`) |

`24-about-page` — About ведёт на `saucelabs.com` (403).
`28-reset-app-state:2` — второй сабтест пропускается: `SAUCE_DEMO_BUG` (Reset App State требует page reload, который на GitHub Pages перенаправляет на логин).

Firefox и WebKit сконфигурированы, но в последнем прогоне не проверялись.

## 🛠 Стек

- **Playwright** — v1.40+
- **Playwright for .NET** — v1.63 (C#-набор, xUnit)
- **Node.js** — v18+
- **Python** — v3.12+
- **pytest** — v9+
- **pytest-rerunfailures** — v16+
- **Java** — 17+
- **Selenium** — 4.38 (Java-набор)
- **JUnit 5** — 5.11
- **Maven** — 3.6+
