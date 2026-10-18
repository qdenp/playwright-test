# E2E Тестовый Фреймворк — Sauce Demo

## Обзор

Репозиторий: https://github.com/qdenp/playwright-test

Шестиязычный E2E-фреймворк с 29 тестовыми сценариями, покрывающими полный функционал Sauce Demo (аутентификация, каталог, корзина, чекаут, адаптивность, навигацию, производительность). Все 6 реализаций поддерживают паритет сценариев — исправления вносятся сначала в TypeScript (эталон), затем зеркалируются в остальные.

## Реализации

| Язык | Фреймворк | Тест-раннер | Стек |
|---|---|---|---|
| TypeScript | Playwright Test | `npx playwright test` | Page Object, web-first assertions |
| JavaScript | Playwright Test | `npx playwright test` | Page Object, web-first assertions |
| Python | Playwright + pytest | `pytest` | Page Object, pytest fixtures |
| C# | Playwright for .NET | `dotnet test` | xUnit, Page Object, web-first assertions |
| Java (Selenium) | Selenium WebDriver | `mvn test` | JUnit 5, Chrome DevTools, `--disable-features=BackforwardCache` |
| Java (Playwright) | Playwright for Java | `mvn test` | JUnit 5, Page Object, web-first assertions |

## Покрытие (29 сценариев)

- **01–06**: Аутентификация (логин, неверный пароль, заблокированный пользователь, параметризованный логин, logout)
- **07–12**: Каталог (сортировка, детали товара, изображения, карточки)
- **13–19**: Корзина (добавление, удаление, изменение количества, комбинации)
- **20–22**: Чекаут (полный флоу, валидация, экспорт PDF)
- **23–26**: Навигация (hamburger menu, адаптивность, back/forward)
- **27**: Бюджеты производительности (warmup + 3-run averaging, re-login)
- **28**: Сброс состояния приложения
- **29**: Социальные ссылки (новые вкладки)

## Результаты (локально, Chromium)

| Реализация | Всего | Passed | Skipped | Время |
|---|---|---|---|---|
| TypeScript | 58 | 56 | 2 | 48с |
| JavaScript | 58 | 56 | 2 | 47с |
| Python | 62 | 59 | 3 | 107с |
| C# | 58 | 56 | 2 | 60с |
| Java (Selenium) | 58 | 58 | 2 | 2:55 мин |
| Java (Playwright) | 58 | 58 | 2 | 2:14 мин |

Skipped: `24-about-page` (About → saucelabs.com, 403), `28-reset-app-state:2` (SAUCE_DEMO_BUG)

## Архитектурные решения

- **Web-first assertions** — `expect(locator).toHaveCount()`, `toHaveText()` с auto-retry вместо `waitForTimeout`/`Thread.sleep`
- **Page Object Pattern** — локаторы и бизнес-логика вынесены в отдельные классы, спеки содержат только вызовы методов PO
- **Перелогин для perf-тестов** (`Test27`) — SPA на GitHub Pages не поддерживает `page.reload()` для аутентифицированных страниц, поэтому используется re-login через `resetToLoginPage()` (Selenium) / `loginPage.goTo() + loginPage.login()` (Playwright)
- **Warmup + averaging** (§3.2) — 1 прогрев + 3 измерения + усреднение

## CI/CD

| Workflow | Сборка | Язык |
|---|---|---|
| `selenium-java.yml` | Maven (Temurin 17) | Java Selenium |
| `playwright-java.yml` | Maven (Temurin 17) | Java Playwright |
| `typescript.yml` | npm + Playwright | TypeScript |
| `js.yml` | npm + Playwright | JavaScript |
| `python.yml` | pytest + Playwright | Python |
| `csharp.yml` | dotnet 10.0 | C# |
| `deploy.yml` | GitHub Pages | Vite SPA