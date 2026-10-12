# Code Review: playwright-tests

**Дата:** 2026-10-12
**Охват:** `typescript/` (35 спеков, 4 PO, fixtures, config), выборочно `python/`, `js/`

---

## Общая оценка

| Аспект | Оценка | Комментарий |
|---|---|---|
| Архитектура (PO) | 7/10 | PO есть везде, но методы дублируются и в тесты «протекают» |
| Надёжность | 4/10 | Много fetch-first ассертов, sleep, замеры через `Date.now()` |
| Корректность | 5/10 | Есть падающие и бесполезные тесты |
| Консистентность | 4/10 | 3 реализации (TS/JS/Py) с расхождениями |
| Maintainability | 5/10 | Мёртвый код, дублирование данных |

Архитектурно сделано добротно (Page Object, вынесенные данные, отдельные фикстуры),
но есть несколько реальных багов и системных анти-паттернов.

---

## Критические проблемы (P0)

### 1. `authenticatedPage` с `{ auto: true }` — двойной логин

`typescript/tests/fixtures.ts:35`

```ts
authenticatedPage: [
  async ({ page }, use) => {
    await loginPage.goTo();
    await loginPage.login();
    await inventoryPage.waitForPageLoad();
    await use({ page, loginPage, inventoryPage, cartPage, checkoutPage });
  },
  { auto: true },
],
```

Фикстура выполняется для **всех** тестов, даже если тест её не использует.

`typescript/tests/e2e/01-login.spec.ts` работает через `page`, но фикстура уже выполнила
`goTo()` + `login()` до старта тела теста. Итог: лишняя навигация и лишний логин в каждом
тесте, включая `02-invalid-password`, где фикстура сперва успешно логинит `standard_user`,
после чего тест логинится заново с неверным паролем. Лишние секунды каждому тесту плюс риск
загрязнения состояния (например, остаточный badge корзины).

**Фикс:** убрать `auto: true`, оставить явное использование `async ({ authenticatedPage })`.
Если нужен общий вход — использовать `storageState`.

### 2. `16-visual-screenshot.spec.ts` — скриншот никак не проверяется

`typescript/tests/e2e/16-visual-screenshot.spec.ts:10,16`

```ts
await expect(page).toHaveURL(/.*inventory\.html/);
await page.screenshot({ path: "test-results/screenshots/products-page.png", fullPage: false });
```

Единственный `expect` — проверка URL, которую фикстура `authenticatedPage` уже гарантировала
своей навигацией. Сам скриншот не сравнивается ни с чем: просто пишется в файл.
Тест выглядит проверкой, но фактически ничего не валидирует.
Заменить на `await expect(page).toHaveScreenshot("products-page.png")` со снапшотом в `git`,
либо убрать из набора.

### 3. `30-api-intercept.spec.ts:18-26` — тест заведомо падает

```ts
const responses: any[] = [];
page.on("response", ...);
expect(responses.length).toBeGreaterThan(0);
```

Подписка навешивается **после** того, как `authenticatedPage` уже провёл навигацию.
Ни одного нового запроса не последует — `responses` пустой, ассерт `0 > 0` падает.

Плюс сам смысл теста «verify API response» не выполнен: в SauceDemo нет API для товаров,
страница рендерится сервером. Переписать как проверку отсутствия ошибок в консоли
(`page.on('console')`) или удалить.

**Статус: закрыто.** `30-api-intercept.spec.ts`, `.js` и `test-30-api-intercept.py` удалены
из всех трёх реализаций, паритет набора — 30/30/30. Если такой тест появится снова,
не переписывать «в лоб».

### 4. `12-cart-quantity.spec.ts` — тест не тестирует quantity

`typescript/tests/e2e/12-cart-quantity.spec.ts`

Название «Изменение количества товара в корзине», но по факту: добавить → открыть корзину →
`toHaveCount(1)`. Количество не меняется и не проверяется.

При этом в `27-quantity-race-condition.spec.ts:5` корректно задокументировано, что quantity
в SauceDemo редактировать нельзя — значит тест 12 должен был бы проверять **отсутствие** поля
редактирования, а не счётчик. Сейчас он дублирует логику тестов 04/07.

---

## Системные анти-паттерны (P1)

### 5. Fetch-first ассерты вместо web-first

```ts
expect(await inventoryPage.getProductCount()).toBe(6);   // 01-login.spec.ts:18
const prices = await inventoryPage.getAllProductPrices();
expect(prices[i]).toBeGreaterThanOrEqual(prices[i - 1]); // 05-sort-by-price.spec.ts:16
```

Нет авто-ретраев — единственная попытка прочитать DOM. Предпочтительно web-first:

```ts
await expect(inventoryPage.products).toHaveCount(6);
```

### 6. `page.waitForTimeout(500)` в PO

`typescript/tests/page-objects/CartPage.ts:63-64`

```ts
await removeButton.click();
// Не ждём detached — просто даём время на обновление
await this.page.waitForTimeout(500);
```

Комментарий в коде прямо признаёт проблему. Правильно —
`await expect(this.cartItems).toHaveCount(n-1)` либо
`locator.waitFor({ state: "detached" })`.

### 7. Замеры производительности через `Date.now()`

`typescript/tests/e2e/33-performance-budget.spec.ts`

Без warmup-прогона; на холодном старте WebKit/Firefox первый `page.goto` гарантированно
флакит. Для практики допустимо, но это не «performance budget», а «случайное число».
Минимум — убрать первый тест и гонять 3+ итераций с усреднением.

### 8. Глушение ошибок

`typescript/tests/page-objects/CartPage.ts:84-93` — `try { ... } catch { break; }` в
`clearCart()` маскирует реальные сбои.

---

## Архитектура и консистентность (P1)

### 9. Дубликат локаторов

`typescript/tests/page-objects/InventoryPage.ts:24-25`

```ts
this.cartBadge = this.page.locator(".shopping_cart_link");
this.cartIcon = this.page.locator(".shopping_cart_link");
```

Один и тот же локатор под разными именами, причём `cartBadge` — это **ссылка на корзину**,
а не badge (badge — вложенный `.shopping_cart_badge`). Именование вводит в заблуждение.
`cartIcon` — мёртвое поле.

### 10. Нарушение PO — логика тестов обходит page objects

- Тесты напрямую обращаются к `inventoryPage.cartBadge.locator(...)`,
  `page.locator('.inventory_item_img')`, `page.getByText(...)`
- В `InventoryPage` (TS) **нет** методов `removeProductFromCart` / `resetAppState`,
  хотя тесты `07-remove-from-cart` и `34-reset-app-state` существуют.
  В Python `reset_app_state` реализован (`python/tests/page_objects/inventory_page.py:88`),
  в TS — нет. Асимметрия реализаций.

### 11. Три реализации — это осознанное решение

TS, JS и Python написаны трижды **намеренно**: практика на разных стеках и портфолио.
Сведение к одной реализации — **не** вариант. Что здесь реально важно:

- Дублирование **покрытия** между наборами — by design, а не дефект.
- `js/` при этом дублирует `typescript/` в пределах одного языка. Это нормально для
  практики, но помните: это два отдельных набора с двумя `node_modules`, и правку спеков
  нужно вносить в оба.
- Единственная настоящая проблема здесь — **рассинхронизация** (см. §14), а не количество
  реализаций.

### 12. Непоследовательность импортов

В `tests/e2e/` встречается и `from "../fixtures"`, и `from "../fixtures.js"`
(например `08-parameterized-login.spec.ts:1` против `06-full-purchase-flow.spec.ts:1`).
При `"type": "module"` корректный вариант — `.js`. Унифицировать и зафиксировать
в ESLint/tsconfig.

### 13. Стиль локаторов

Смешаны CSS-цепочки `.btn.btn_primary.btn_small.btn_inventory` и `data-test="..."`.
В SauceDemo `data-test` доступны и для кнопок корзины — он стабильнее к рефакторингу
вёрстки. Привести к `data-test`.

### 14. Расхождения реализаций JS-семейства vs Python

| Что | TypeScript / JavaScript | Python |
|---|---|---|
| `reset_app_state` | нет метода в `InventoryPage` | есть (`inventory_page.py:88`) |
| `get_product_price` | `parseFloat` + ошибка при отсутствии | `text_content()` может вернуть `None` |
| Параметры логина | массив в `config.ts` / `config.js` | функция `get_login_params()` в `conftest.py` |
| Credentials из env | `SAUCE_DEMO_USERNAME` / `SAUCE_DEMO_PASSWORD` | `SAUCE_DEMO_PASSWORDS` (формат `user:pass`) |

Разные схемы конфигурации уже привели к разному поведению наборов.

---

## Низкоприоритетные (P2)

- `getAllCredentials()` и `getLoginParams()` мертвы — никем не вызываются (`config.ts:98,109`)
- `loginTests` и `users` дублируют данные о пользователях (`config.ts:36-93`)
- В `loginTests` для failure-кейсов бессмысленное `expectedTitle: "Products"` (`config.ts:82,88`)
- `getByText("Back to Products")` может матчить несколько узлов → лучше
  `getByRole('button', { name: 'Back to Products' })` (`InventoryPage.ts:160`)
- `:has-text("${productName}")` ломается на товарах-подстроках (`InventoryPage.ts:56`)
- Нет проверки `item total` / `tax` / `total` в чекауте (`06-full-purchase-flow.spec.ts:29`)
- Нет тегов (`@smoke`, `@regression`) и `beforeEach` для скриншотов
- Нет CI-конфига, хотя `playwright.config.ts` опирается на `process.env.CI`
- Python: файлы тестов с дефисами (`test-01-login.py`) невалидны как имена модулей
- Python: `text_content()` может вернуть `None` — типы не защищены (`inventory_page.py:41-53`)

---

## План исправлений по приоритету

1. ~~**Убрать `auto: true`** из фикстуры~~ — **сделано 2026-10-12.**
   Ни один тест не зависел от неявного авто-логина: после снятия все три набора
   остались зелёными. Логин теперь выполняется только в 22 файлах, которым он
   реально нужен.
2. ~~Починить/удалить `30-api-intercept` и `16-visual-screenshot`~~ —
   **сделано 2026-10-12:** оба удалены из всех трёх реализаций.
3. ~~**Переписать `12-cart-quantity`**~~ — **сделано 2026-10-12.**
   Тест переписан на реальное поведение SauceDemo: badge корзины +1 при добавлении,
   −1 при удалении, badge отсутствует на пустой корзине. Проверка «quantity нельзя
   изменить» осталась в тесте 27, где ей и место — дубль устранён.
4. ~~Перевести ассерты на **web-first** (`toHaveText`, `toHaveCount`, `toHaveScreenshot`)~~ —
   **сделано 2026-10-12.** Из спеов убраны fetch-first `expect(await x())`,
   `resolves.toBe(...)` и сырые `page.locator(...)`.
   Осознанно оставлено как есть: замеры `loadTime` через `Date.now()` в тестах 33/34
   (это измерение, а не проверка состояния UI) и сравнение массива отсортированных
   цен/имён в тестах 05/14 (массив целиком — принципиально snapshot-проверка).
5. Убрать `waitForTimeout` / `try-catch` из `CartPage`. — **сделано 2026-10-12.**
6. Добавить в `InventoryPage` методы `removeProductFromCart`, `resetAppState`,
   привести локаторы к `data-test`. — **сделано 2026-10-12** (`resetAppState`,
   `removeItem`, единый `cartBadgeCount`).
7. ~~Добавить проверку сумм в чекауте~~ — **сделано 2026-10-12** (item total →
   tax → total).
8. **Три реализации не трогать** — это осознанная практика и портфолио.
   Пайритовая задача: выровнено число тестов.
   TS и JS теперь дают ровно 57/57. Python — 61: это параметризация теста 20
   по 5 пользователям, тогда как TS/JS делают тот же перебор циклом в одном тесте.
   Разница структурная и осознанная, не дефект.
9. Для скорости — внедрить `storageState` — **проверено и отклонено 2026-10-12.**
   Корзина в SauceDemo лежит в `localStorage` (`cart-contents`), а не в серверной
   сессии, поэтому копирование состояния через `storageState()` переносило бы
   товары между тестами. Плюс cookie `session-username` живёт ровно 10 минут
   и не продлевается — на CI прогон рано или поздно выйдет за этот срок.
   Подробности с замерами — ниже.
10. ~~Убрать мёртвый код из `config.ts`~~ — **сделано 2026-10-12** (`User`, `users`,
    `getAllCredentials`, `getLoginParams`). Credentials остались в Python (живые).

---

## Прогресс (обновлено 2026-10-12)

| Набор | Результат |
|---|---|
| TypeScript | `tsc` чистый, **54 passed / 1 skipped** |
| JavaScript | **55 passed / 1 skipped** |
| Python | **60 passed / 1 skipped** |

Закрыто по ходу baseline:

- `13-add-remove-all.spec.ts` — убран невалидный `{ timeout }` в 3-м аргументе `test()`
- `18-about-page.spec.ts` — переписан в корректный `test.skip` с причиной (About ведёт
  на `saucelabs.com`, отдающий 403); из TS/JS убран дубль `(disabled)` с
  `waitForTimeout(2000)` и `try/catch`
- `30-api-intercept` — удалён из всех трёх реализаций
- `16-visual-screenshot` — удалён из всех трёх реализаций (скриншот не проверял ничего;
  автоматический `screenshot: 'only-on-failure'` в конфигах оставлен намеренно)
- **Найденный по ходу баг:** в `playwright.config.ts` тест-таймаут был указан как
  `use: { timeout: 120000 }`. Playwright молча игнорирует этот ключ внутри `use` —
  фактически все тесты шли с дефолтными 30с. Вынесен на верхний уровень, проверено
  (`testInfo.timeout` = 120000).
- **Найденный по ходу баг:** `LoginPage.goTo()` в TS ждал `load` со всеми ресурсами,
  из-за чего под нагрузкой навигации зависали на 30с. Приведён к
  `waitUntil: "domcontentloaded"` + `timeouts.NAVIGATION`, как в JS и Python.
- Хардкод `timeout: 15000` в `InventoryPage.goTo()` заменён на `timeouts.NAVIGATION`
  во всех трёх реализациях.
- `workers` в TS-наборе ограничен до 3: saucedemo.com троттлит параллельные подключения,
  и на 6 воркерах часть навигаций стабильно падала по таймауту.
- `auto: true` снята с фикстуры `authenticatedPage` в TS: логин выполнялся в каждом
  тесте, даже не требующем авторизации (22 файла реально нуждаются, 7 логинятся сами).
- Тест `12-cart-quantity` переписан на арифметику корзины (+1/−1/badge отсутствует),
  вместо неработающей проверки «изменение quantity».
- Из `CartPage` во всех трёх реализациях убран мёртвый код с несуществующими локаторами
  (`[data-test="quantity"]`, `.summary_total_label`, `CartItem`).
- Убран `page.waitForTimeout(500)` из `CartPage.removeItem()` — синхронизация теперь
  через web-first `toHaveCount` в тесте.
- `InventoryPage`: удалён дубль `cartIcon`, добавлен единый локатор `cartBadgeCount`
  (`.shopping_cart_badge`) — и для web-first ассертов, и внутри `getCartBadgeCount()`.

Второй заход (пункт 4 плана — web-first ассерты):
- Из всех трёх наборов убраны fetch-first ассерты: `expect(await x())`,
  `.resolves.toBe(...)` на методах PO и сырые `page.locator(...)` внутри спеков.
- Счётчики, badge и видимость переведены на `toHaveCount` / `toHaveText` /
  `toBeVisible` — с авто-ретраями.
- Содержимое («имя непустое», «цена содержит $», «есть src») — на
  `not.toBeEmpty()` / `toContainText` / `toHaveAttribute`.
- В PO добавлены отсутствовавшие локаторы (`productNames`, `productDescriptions`,
  `productPrices`, `productImages`, `addToCartButtons`, ссылки меню), чтобы убрать
  сырые локаторы из спеков.
- В `CheckoutPage` все три реализации сведены к одной схеме с `completeTitle`
  и добавлен `generatePdf()`; из JS и Python убран неработавший `.complete-text`.
- Замеры `loadTime` через `Date.now()` (тесты 33/34) и сравнение отсортированных
  массивов (05/14) оставлены как есть — это не проверка состояния UI.

Третий заход (`CartPage.waitForPageLoad()` и лишние ожидания):

- `CartPage.waitForPageLoad()` во всех трёх реализациях переведён с ожидания
  `.cart_item.first()` на ожидание URL `cart.html`. Раньше на пустой корзине метод
  зависал до таймаута. Проверено пробой: 37 мс против зависания.
- Побочный эффект правки: старый `.first()` **случайно** маскировал гонку в тестах
  28/29 — они читали `getCartItemNames()` (snapshot без ретрая) сразу после
  `openCart()`, и DOM не всегда успевал отрисоваться. Найдено прогоном: 4 падения.
  Исправлено правильно — перед чтением добавлен web-first `toHaveCount(3)` / `toHaveCount(6)`.
- Проверено и **признано нецелесообразным**: удаление «избыточных» `waitForPageLoad()`
  после `openCart()` (который уже ждёт URL). Замер тробы трижды: with-wait 34–36 мс,
  without-wait 52–63 мс — экономии нет, отличие в пределах шума. Правка не делалась,
  чтобы не плодить изменения без выигрыша.

Четвёртый заход (паритет числа тестов и мёртвый код):

- **Найденный по ходу баг:** в JS и Python `LoginPage.login()` использовал
  `username || config.username`. Пустая строка falsy, поэтому кейс «пустой username»
  подменялся на `standard_user` — тест был зелёным, не проверив задуманное.
  Заменено на `??` (JS) и `is not None` (Python). TS такой подменёй не страдал
  (у него обычные default-параметры) — там кейса просто не было в данных.
- Пробой подтверждено реальное поведение: пустой username → ошибка
  «Epic sadface: Username is required», остаёмся на странице логина.
  Поэтому в JS/Python кейс исправлен на `shouldLogin: false`, а в TS добавлен —
  все три набора теперь гоняют все 8 кейсов параметризации.
- Мёртвый код в `typescript/tests/config.ts` удалён: интерфейс `User`, массив
  `users`, `getAllCredentials()`, `getLoginParams()` — никем не использовались.
- В Python `conftest.py` убраны неиспользуемые `valid_users` и лишняя ветка
  `get_login_params`; сами `get_all_credentials` и `get_login_params` живые
  (используются тестами 03, 08, 20) и оставлены.

---

## Итог

Что было сделано за три захода:

1. Устранены настоящие баги: игнорировавшийся тест-таймаут в конфиге, двойной
   логин через `auto: true`, зависание `CartPage.waitForPageLoad()` на пустой
   корзине.
2. Удалены два мёртвых теста и один заведомо падающий.
3. Все сборы переведены на web-first ассерты убраны fetch-first чтения и сырые
   локаторыв спеках; мёртвый код в PO вычищен.
4. Чекаут теперь по-настоящему проверяет суммы: item total → tax → total.
5. Пайтритет числа тестов восстановлен: TS и JS дают 57/57, Python 61 с осознанной
   параметризацией теста 20.

Ключевой вывод по замечаниям «на первый взгляд безобидным»: две из них скрывали
реальные дефекты. Снятие `auto: true` показало, что ни один тест от неё не зависел;
а перевод `waitForPageLoad()` на URL вскрыл гонку в тестах 28/29, где snapshot-чтение
шло без ожидания отрисовки. Для проверки таких правок прогон обязателен.
