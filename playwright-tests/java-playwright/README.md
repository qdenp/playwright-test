# Java Playwright Tests

E2E-тесты для Sauce Demo на **Playwright for Java + JUnit 5 + Maven**.

## Стек

| Компонент | Версия |
|---|---|
| Java | 17+ |
| Playwright for Java | 1.50+ |
| JUnit 5 | 5.11+ |
| Maven | 3.6+ |

## Запуск

```bash
cd playwright-tests/java-playwright
mvn test -Dheadless=true
```

Для одного теста:
```bash
mvn test -Dtest=Test01Login
```

## CI

GitHub Actions: `.github/workflows/playwright-java.yml`

## Архитектура

```
src/test/java/
├── config/
│   ├── TestConfig.java      # Константы: креды, таймауты, бюджеты
│   └── PlaywrightConfig.java # Playwright browser context config
├── base/
│   └── BaseTest.java        # Playwright fixtures, setupAuthenticatedPage()
├── pageobjects/
│   ├── LoginPage.java
│   ├── InventoryPage.java
│   ├── CartPage.java
│   ├── CheckoutPage.java
│   └── ItemDetailPage.java
└── e2e/                     # 29 классов сценариев (Test01-Test29)
```

## Особенности

- **Page Object Pattern** — все локаторы в page-objects
- **Web-first assertions** — Playwright auto-retry assertions
- **Performance budgets (Test27)** — warmup + 3-run averaging с перелогином
- **localStorage-based auth** — продукты предзагружаются в localStorage для GitHub Pages
- **No `SAUCE_DEMO_TIMEOUT` env override** — таймауты жёстко заданы в `TestConfig.java`