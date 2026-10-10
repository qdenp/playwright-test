# Playwright E2E Tests — Sauce Demo

Комплексные E2E-тесты для [Sauce Demo](https://www.saucedemo.com/) на **JavaScript** и **Python** с использованием [Playwright](https://playwright.dev/).

## 📋 Содержание

| # | Описание | JS | Python |
|---|----------|-----|--------|
| 01 | Успешный логин | ✅ | ✅ |
| 02 | Неверный пароль | ✅ | ✅ |
| 03 | Блокированный пользователь | ✅ | ✅ |
| 04 | Добавление товара в корзину | ✅ | ✅ |
| 05 | Сортировка по цене | ✅ | ✅ |
| 06 | Полный flow покупки | ✅ | ✅ |
| 07 | Удаление из корзины | ✅ | ✅ |
| 08 | Параметризованный логин | ✅ | ✅ |
| 09 | Скриншот при падении | ✅ | ✅ |
| 10 | Кросс-браузерная проверка | ✅ | ✅ |
| 11 | Валидация checkout | ✅ | ✅ |
| 12 | Изменение количества | ✅ | ✅ |
| 13 | Добавление/удаление всех товаров | ✅ | ✅ |
| 14 | Сортировка по имени | ✅ | ✅ |
| 15 | Hamburger меню | ✅ | ✅ |
| 16 | Визуальный скриншот | ✅ | ✅ |
| 17 | Logout | ✅ | ✅ |
| 18 | About page | ✅ | ✅ |
| 19 | Item detail | ✅ | ✅ |
| 20 | Разные пользователи = одинаковый каталог | ✅ | ✅ |
| 21 | Экспорт PDF заказа | ✅ | ✅ |

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

## 📊 Архитектура

```
playwright-tests/
├── js/
│   ├── playwright.config.js      # Конфиг Playwright (retries, browsers)
│   ├── tests/
│   │   ├── config.js             # Пользователи и credentials из env vars
│   │   ├── fixtures.js           # Base fixtures
│   │   ├── e2e/                  # 21 тестовый файл (01-21)
│   │   └── page-objects/         # LoginPage, InventoryPage, CartPage, CheckoutPage
├── python/
│   ├── pytest.ini                # Конфиг pytest (retries, browsers)
│   ├── requirements.txt
│   └── tests/
│       ├── conftest.py           # Shared fixtures
│       ├── e2e/                  # 21 тестовый файл (01-21)
│       └── page_objects/         # Page Objects
└── ENV_CONFIG.md                 # Документация по env vars
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
- **Sequential numbering** — тесты пронумерованы 01-21 для удобства

## 📈 Результаты

| Браузер | Статус |
|---------|--------|
| Chromium | ✅ 87/87 passed |
| Firefox | ✅ 87/87 passed |
| WebKit | ✅ 87/87 passed |

## 🛠 Стек

- **Playwright** — v1.40+
- **Node.js** — v18+
- **Python** — v3.12+
- **pytest** — v9+
- **pytest-rerunfailures** — v16+
