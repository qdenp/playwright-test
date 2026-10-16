# SauceDemo e2e tests — Java

E2E-тесты [SauceDemo](https://www.saucedemo.com) на **Java 17 + Selenium WebDriver 4.x + JUnit 5 + Maven**.
Это четвёртая реализация одного и того же набора сценариев: TypeScript (`playwright-tests/typescript`,
эталон), JavaScript (`playwright-tests/js`), Python (`playwright-tests/python`) и Java (этот каталог).

## Стек

| Компонент | Версия | Назначение |
|---|---|---|
| Java | 17 | язык и тулчейн |
| Selenium WebDriver | 4.38.0 (`selenium-java`) | управление браузером |
| JUnit Jupiter | 5.11.4 (`junit-jupiter`) | runner, параметризация |
| Maven Surefire | 3.5.2 | запуск тестов |
| Chrome (headless) | `/usr/bin/google-chrome` | единственный браузер |

## Как запускать

```bash
cd playwright-tests/java
mvn test            # полный прогон, 58 тестов (29 сценариев, 1 из них skip)
mvn test -Dtest=LoginTest              # один класс
mvn test -Dtest='SortByPriceTest#*low*' # один тест
```

### Переменные окружения

| Переменная | По умолчанию | Назначение |
|---|---|---|
| `SAUCE_DEMO_USERNAME` | `standard_user` | пользователь для логина в большинстве тестов |
| `SAUCE_DEMO_PASSWORD` | `secret_sauce` | его пароль |
| `SAUCE_DEMO_BASE_URL` | `https://www.saucedemo.com` | URL стенда |
| `CHROME_BINARY` | `/usr/bin/google-chrome` | путь к бинарю Chrome |
| `SAUCE_DEMO_TIMEOUT` | `10` | обычный таймаут ожиданий, с |
| `SAUCE_DEMO_NAVIGATION_TIMEOUT` | `30` | таймаут навигаций, с |
| `SAUCE_DEMO_SLOW_TIMEOUT` | `60` | бюджет для `performance_glitch_user`, с |

Значения по умолчанию — публичный демо-стенд, реальных секретов в репозитории нет.

## Структура

```
src/test/java/
├── config/TestConfig.java        таймауты, базовый URL, креды из env, EXPECTED_PRODUCT_COUNT = 6
├── base/BaseTest.java            @BeforeEach/@AfterEach: headless Chrome, навигация, логин-хелперы
├── pageobjects/
│   ├── LoginPage.java
│   ├── InventoryPage.java
│   ├── CartPage.java
│   ├── CheckoutPage.java
│   └── UiActions.java            клик «после остановки» анимации бургер-меню
└── e2e/
    ├── LoginTest.java … SocialMediaLinksTest.java   (29 классов, по одному на сценарий)
```

## Принятые решения (отличия от Playwright-референса)

* **Ожидания.** Playwright-ассерты с авто-ретраем заменены на `WebDriverWait` +
  `ExpectedConditions`-ожидания в Page Objects (`waitForCartBadgeCount`,
  `waitForCartItemCount`, `waitForProductCount`, `waitForItemQuantities` …).
  `Thread.sleep` не используется нигде.
* **Бургер-меню.** Пункты меню появляются с CSS-переходом, поэтому прямой
  `click()` сразу после открытия меню падает с `ElementNotInteractable`.
  `UiActions.click` ждёт, пока позиция элемента перестанет меняться, и только
  потом кликает — эквивалент неявной проверки actionability в Playwright.
* **Деньги.** `assertEquals(a, b, 0.005)` соответствует `toBeCloseTo(x, 2)`
  в Playwright (погрешность в полцента, суммы округляются до копеек).
* **Скачивание PDF.** Playwright ловит событие `download`; здесь headless Chrome
  скачивает в отдельную временную папку (через prefs `download.default_directory`),
  тест ждёт появления файла и проверяет его размер и магические байты `%PDF-`.
* **`about-page`** — `@Disabled` с причиной: ссылка ведёт на saucelabs.com, который
  отдаёт 403 (зеркалит `test.skip` в референсах).
* **`.ignore-certificate-errors`** в опциях Chrome: в образе CI трафик идёт через
  TLS-инспектирующий прокси, корневой сертификат которого лежит в системном
  trust store, а Chrome пользуется собственным хранилищем.
* Тесты не логинятся в `@BeforeEach` — каждый тест создаёт своё состояние сам.
* Номера сценариев не попадают в имена тестов: номер остаётся в таблице ниже.

## Соответствие сценариев

| Референс (JS/TS/Py) | Класс Java | Тестов | Примечание |
|---|---|---|---|
| 01-login | `LoginTest` | 1 | |
| 02-invalid-password | `InvalidPasswordTest` | 1 | |
| 03-locked-user | `LockedUserTest` | 1 | |
| 04-parameterized-login | `ParameterizedLoginTest` | 8 | `@ParameterizedTest`, 5 пользователей + 3 некорректных пары |
| 05-logout | `LogoutTest` | 1 | |
| 06-logout-from-pages | `LogoutFromPagesTest` | 2 | |
| 07-sort-by-price | `SortByPriceTest` | 2 | low→high, high→low |
| 08-sort-by-name | `SortByNameTest` | 1 | |
| 09-item-detail | `ItemDetailTest` | 1 | |
| 10-multiple-users-catalog | `MultipleUsersCatalogTest` | 1 | 5 пользователей в одном тесте |
| 11-product-images | `ProductImagesTest` | 1 | |
| 12-product-cards | `ProductCardsTest` | 1 | |
| 13-add-to-cart | `AddToCartTest` | 1 | |
| 14-remove-from-cart | `RemoveFromCartTest` | 1 | |
| 15-cart-quantity | `CartQuantityTest` | 1 | пустая корзина → badge 0 |
| 16-add-remove-all | `AddRemoveAllTest` | 1 | |
| 17-quantity-race-condition | `QuantityRaceConditionTest` | 2 | второй кликает все кнопки «Add to cart» одним тиком через JS |
| 18-item-combinations | `ItemCombinationsTest` | 3 | 3 дешёвых / 3 дорогих / через один |
| 19-add-all-six-products | `AddAllSixProductsTest` | 3 | |
| 20-full-purchase-flow | `FullPurchaseFlowTest` | 1 | item total → tax (8%) → total |
| 21-checkout-validation | `CheckoutValidationTest` | 1 | First/Last Name → Postal Code |
| 22-export-pdf | `ExportPdfTest` | 1 | проверяется скачанный PDF-файл |
| 23-hamburger-menu | `HamburgerMenuTest` | 1 | |
| 24-about-page | `AboutPageTest` | 1 | `@Disabled` — saucelabs.com отдаёт 403 |
| 25-responsive-design | `ResponsiveDesignTest` | 5 | 375×667, 768×1024, resize |
| 26-browser-navigation | `BrowserNavigationTest` | 4 | back/forward inventory ↔ cart ↔ checkout |
| 27-performance-budget | `PerformanceBudgetTest` | 6 | 3 c / 5 c бюджеты, усреднение за 3 прогона |
| 28-reset-app-state | `ResetAppStateTest` | 2 | |
| 29-social-media-links | `SocialMediaLinksTest` | 3 | ссылки, `target=_blank`, новая вкладка |

Итого: **29 сценариев → 29 классов → 58 тестовых методов**, из них 1 (`AboutPageTest`)
помечен `@Disabled`.

## Особенности SauceDemo, учтённые в наборе

* `locked_out_user` всегда получает ошибку входа (`03-locked-user`, `04-parameterized-login`).
* `performance_glitch_user` логинится намеренно медленно — для него используется
  `SAUCE_DEMO_SLOW_TIMEOUT` (60 c).
* Количество товара в корзине не редактируется — тесты проверяют **отсутствие**
  поля ввода, а не его изменение (`17-quantity-race-condition`).
* Пустая корзина не содержит badge: `getCartBadgeCount()` возвращает `0`, а не падает.
* Export PDF легитимен: кнопка `Generate PDF order` есть на `checkout-complete.html`.
* Нет API товаров: страницы рендерятся на сервере, поэтому никаких `page.route`.
