---
name: playwright-tests
description: Работа с e2e-тестами SauceDemo в playwright-tests/ (TS, JS, Python). Использовать при правке спеков, page objects, фикстур, запуске тестов, ревью качества тестов и синхронизации трёх реализаций.
---

# Skill: playwright-tests

Набор e2e-тестов [SauceDemo](https://www.saucedemo.com) в трёх реализациях.

---

## Карта проекта

| Реализация | Корень | Page Objects | Тесты | Конфиг |
|---|---|---|---|---|
| TypeScript | `playwright-tests/typescript/` | `tests/page-objects/*.ts` | `tests/e2e/*.spec.ts` | `playwright.config.ts`, `tests/config.ts` |
| JavaScript | `playwright-tests/js/` | `tests/page-objects/*.js` | `tests/e2e/*.spec.js` | `tests/config.js` |
| Python | `playwright-tests/python/` | `tests/page_objects/*.py` | `tests/e2e/test-*.py` | `pytest.ini`, `tests/constants.py` |

Общие элементы TS/JS:

- Фикстура `authenticatedPage` (`tests/fixtures.ts`) — предустановленный логин.
  Запрашивается явно, `auto` снят: тесты, которым страница логина не нужна,
  используют `page`. В JS-фикстуре отдаются только `page`, `loginPage`,
  `inventoryPage` — `CartPage`/`CheckoutPage` там создают вручную.
- Параметры логина — `tests/config.ts` (`loginTests`). Пользователи из `users`
  удалены из TS вместе с `getAllCredentials()`; в Python `get_all_credentials` жив.
- Page Objects: `LoginPage`, `InventoryPage`, `CartPage`, `CheckoutPage`.
- Playwright projects: `chromium`, `firefox`, `webkit`.

---

## Нумерация файлов (сгруппировано по функционалу)

Номер есть **только** в префиксе имени файла. В заголовках `test(...)`,
`test.describe(...)`, `test.skip(...)` и doc-комментариях номеров быть не должно.

| Диапазон | Группа | Файлы |
|---|---|---|
| 01–06 | Логин и доступы | login, invalid-password, locked-user, parameterized-login, logout, logout-from-pages |
| 07–12 | Каталог | sort-by-price, sort-by-name, item-detail, multiple-users-catalog, product-images, product-cards |
| 13–19 | Корзина | add-to-cart, remove-from-cart, cart-quantity, add-remove-all, quantity-race-condition, item-combinations, add-all-six-products |
| 20–22 | Чекаут | full-purchase-flow, checkout-validation, export-pdf |
| 23–26 | Навигация и UI | hamburger-menu, about-page, responsive-design, browser-navigation |
| 27–29 | Прочее | performance-budget, reset-app-state, social-media-links |

Одинаковые номера во всех трёх реализациях. Python-файлы: `test-NN-topic.py`.

---

## Команды запуска

```powershell
# TypeScript (основной набор)
cd playwright-tests\typescript
npx tsc --noEmit                              # типы (в CI — джоба lint)
npx playwright test --project=chromium        # все тесты
npx playwright test 13-add-to-cart            # один файл
npx playwright test -g "purchase"             # по имени
npx playwright test --ui                      # интерактивный режим
npx playwright show-report                    # HTML-отчёт

# JavaScript
cd playwright-tests\js
npx playwright test --project=chromium

# Python
cd playwright-tests\python
pytest                                        # все
pytest tests/e2e/test-20-full-purchase-flow.py
pytest -k login -v
```

Таймауты/ретраи: TS — `actionTimeout: 60000`, `timeout: 120000`, ретраи только на CI.
Python — `--reruns 3 --reruns-delay 1`.

---

## Известное состояние (актуально на 2026-10-12)

| Проблема | Где | Статус |
|---|---|---|
| `npx tsc --noEmit` падал: `timeout` в 3-м аргументе `test()` | `typescript/tests/e2e/16-add-remove-all.spec.ts` | **Закрыта 2026-10-12:** лишний аргумент `{ timeout }` убран |
| `npx tsc --noEmit` падал: `test()` со строкой вместо колбэка | `typescript/tests/e2e/24-about-page.spec.ts` | **Закрыта 2026-10-12:** тест переписан в корректный `test.skip` с причиной |
| Тест `16-visual-screenshot` ничего не проверял: скриншот не сравнивался ни с чем | все три реализации | **Закрыта:** удалён из TS, JS и Python. Если понадобится визуальная проверка — только через `toHaveScreenshot` со снапшотом в `git` |
| Тест-таймаут не применялся: `timeout` лежал в `use`, где Playwright его молча игнорирует (фактически 30с вместо 120с) | `playwright.config.ts` | **Закрыта 2026-10-12:** `timeout` вынесен на верхний уровень |
| `LoginPage.goTo()` в TS ждал `load` со всеми картинками → навигации зависали на 30с под нагрузкой | `typescript/.../LoginPage.ts` | **Закрыта 2026-10-12:** `waitUntil: "domcontentloaded"` + `timeouts.NAVIGATION`, как в JS и Python |
| Хардкод `timeout: 15000` в `InventoryPage.goTo()` | все три реализации | **Закрыта 2026-10-12:** заменён на `timeouts.NAVIGATION` / `NAVIGATION_TIMEOUT` |
| TS-набор запускался на 6 воркерах, и saucedemo.com троттлил параллельные подключения | `playwright.config.ts` | **Закрыта 2026-10-12:** `workers: 3` локально (как в JS-наборе) |
| API-intercept тест заведомо падал (пустой массив responses) | все три реализации | **Закрыта:** удалён из TS, JS и Python |
| `authenticatedPage` была помечена `{ auto: true }` → логин в каждом тесте, даже там где не нужен | `typescript/tests/fixtures.ts` | **Закрыта 2026-10-12:** `auto` убран. Логин делают только 22 файла, которым он реально нужен; 7 файлов логинятся сами |
| Тест `15-cart-quantity` не тестировал quantity: по факту add → count 1 | все три реализации | **Закрыта 2026-10-12:** переписан на реальную арифметику корзины: badge +1 при добавлении, −1 при удалении, отсутствие badge на пустой корзине |
| Мёртвый код в `CartPage`: `quantityInput`, `changeItemQuantity`, `totalPrice`, `getTotalPrice`, `getRemoveButtonCount`, интерфейс `CartItem` | все три реализации | **Закрыта 2026-10-12:** удалены; локальные локаторы `[data-test="quantity"]` и `.summary_total_label` на страницы не существуют |
| Дубль локаторов `cartBadge` / `cartIcon` | `typescript/tests/page-objects/InventoryPage.ts` | **Закрыта 2026-10-12:** `cartIcon` удалён, добавлен `cartBadgeCount` = `.shopping_cart_badge` (единый селектор для web-first проверок) |
| `page.waitForTimeout(500)` в `CartPage.removeItem()` | все три реализации | **Закрыта 2026-10-12:** sleep убран, синхронизация через web-first `toHaveCount` в тесте |
| `CartPage.waitForPageLoad()` ждал `.cart_item.first()` — на пустой корзине зависал до таймаута | все три реализации | **Закрыта 2026-10-12:** ожидание URL `cart.html`. Проверено: 37 мс вместо зависания |
| `LoginPage.login()` в JS/Python подменял пустой username дефолтом (`\|\|`) → кейс «пустой username» был falsely green | JS и Python | **Закрыта 2026-10-12:** `??` / `is not None`. Кейс исправлен на `shouldLogin: false` + «Username is required», в TS кейс добавлен. Все три набора гоняют 8 кейсов |
| Мёртвый код в `typescript/config.ts`: `User`, `users`, `getAllCredentials()`, `getLoginParams()` | TS | **Закрыта 2026-10-12:** удалены. В Python те же имена живые (используются тестами 03, 08, 20) — не путать |

### Паритет числа тестов

| Набор | Собрано (chromium) |
|---|---|
| TypeScript | 57 (56 passed + 1 skipped) |
| JavaScript | 57 (56 passed + 1 skipped) |
| Python | 61 (60 passed + 1 skipped) |

TS и JS паритны. Python собирает больше тестов из-за параметризации теста 20 по
5 пользователям (TS/JS делают тот же перебор циклом внутри одного теста) и 8 кейсов
параметризованного логина в тесте 04. Это осознанная разница реализации, не дефект.
| Fetch-first ассерты вместо web-first (`expect(await x())`, `.resolves.toBe()`, `page.locator()` в спеках) | все три реализации | **Закрыта 2026-10-12:** счётчики, badge, видимость и содержимое переведены на `toHaveCount` / `toHaveText` / `toBeVisible`. Замеры `loadTime` в тесте 27 и сравнение отсортированных массивов в 05/08 остались намеренно |
| `29-social-media-links` не проверял соцссылки: тесты мерили число товаров | все три реализации | **Закрыта 2026-10-12:** тест проверяет href / target / rel / открытие вкладки для `social-x`, `social-facebook`, `social-linkedin` |
| `06-logout-from-pages` «проверял» logout через `toHaveURL(/.*saucedemo/)` — регэксп истинен на любой странице сайта | все три реализации | **Закрыта 2026-10-12:** файл разбит на два теста с настоящей проверкой выхода (исчезновение кнопки логина на inventory + появление login-формы после повторного входа) |
| `17-quantity-race-condition`: «quantity stays at 1» проверялся только счётчиком товаров, «rapid add» шёл последовательными `await` | все три реализации | **Закрыта 2026-10-12:** читается `[data-test="item-quantity"]`, race делается одним `page.evaluate` по всем кнопкам |
| `28-reset-app-state`: второй тест побайтово дублировал первый | все три реализации | **Закрыта 2026-10-12:** дубль убран, второй тест проверяет очистку сохраняемой корзины в `localStorage`, а не badge |
| `27-performance-budget`: `waitForPageLoad()` вызывался после фикстуры, уже загрузившей страницу — замер был no-op (~0 мс) | TS, JS, Python | **Закрыта 2026-10-12:** замеряется холодная навигация, медленные пользователи получают `timeouts.SLOW = 60000` |
| `18-item-combinations` утверждал «cheapest/most expensive» по именам товаров, цены не сверялись | все три реализации | **Закрыта 2026-10-12:** цены каталога читаются **до** ухода в корзину (иначе читаются цены корзины) и сверяются с корзиной |
| `21-checkout-validation`: три `toBeVisible()` на полях ввода — шум, истинный независимо от валидации | все три реализации | **Закрыта 2026-10-12:** проход по шагам First Name → Last Name → Postal Code с проверкой, что шаг не переключается раньше времени |
| `11-product-images`: «all images», но видимость проверялась только у первого | все три реализации | **Закрыта 2026-10-12:** видимость проверяется у каждой из шести картинок |
| `09-item-detail`: комментарий утверждал inline-панель, клик по названию открывает отдельную страницу | все три реализации | **Закрыта 2026-10-12:** тест работает со страницей товара (`inventory-item.html`), через `data-test` |
| `menuOverlay` имел три разных значения: TS `.bm-menu-wrap`, JS `.bm-menu`, Python `.bm-cover` (подложка) — проверка «меню открыто» была бы ложноположительной в JS | все три реализации | **Закрыта 2026-10-12:** единый `.bm-menu-wrap`; имена методов и таймауты приведены к единому виду |
| Номера в заголовках `test(...)` surviving после чистки (искались только двойные кавычки) | TS | **Закрыта 2026-10-12:** 16 заголовков в одинарных кавычках тоже очищены |
| Вложенные `cartBadge.locator('.shopping_cart_badge')` вместо `cartBadgeCount`; магическое `6` вместо `EXPECTED_PRODUCT_COUNT` | TS, JS | **Закрыта 2026-10-12:** заменены на стандартизированный локатор и именованную константу |
| Ложный запрет на PDF-тест в правилах: «экспорт PDF отсутствует в SauceDemo» | `AGENTS.md`, скил, workflows, README старого формата | **Закрыта 2026-10-13:** пробой подтверждено — кнопка "Generate PDF order" есть на `checkout-complete.html`, тест `22-export-pdf` легитимен; правила исправлены |

### Baseline на 2026-10-12 (chromium)

| Набор | Команда | Результат |
|---|---|---|
| TypeScript | `npx tsc --noEmit` + `npx playwright test --project=chromium` | tsc чистый, **55 passed / 1 skipped** |
| JavaScript | `npx playwright test --project=chromium` | **55 passed / 1 skipped** |
| Python | `pytest` | **60 passed / 1 skipped** |

Все три набора перепрогнаны 2026-10-12 после снятия `auto: true` с фикстуры и
переписывания теста 12 — результат тот же, значит ни один тест не зависел
от неявного авто-логина.

Важно про паритет PO: JS-фикстура отдаёт только `{ page, loginPage, inventoryPage }`,
а TS — ещё и `cartPage`, `checkoutPage`. В JS-спеках объекты создаются вручную.
Python-фикстура возвращает только `page`, `login_page`, `inventory_page`.

Полный разбор с приоритетами: `playwright-tests/TEST_REVIEW_2026-10-12.md`.

---

## Правила (кратко; полные — в `AGENTS.md`)

1. Только web-first ассерты: `toHaveCount`, `toHaveText`, `toHaveURL`, `toBeVisible`
   с ретраями. Нельзя `expect(await x())`.
2. Никаких `waitForTimeout` / `try-catch` с `break`.
3. В спеках нет сырых `page.locator(...)` — только PO-методы.
4. Селекторы — `[data-test="..."]`, не CSS-цепочки.
5. Деньги и счётчики — точные значения, не `toBeTruthy()`.
6. Таймауты только из `config.ts` / `constants.py`.
7. Изменение теста = правка в трёх реализациях или фиксация расхождения.
8. Тест обязан что-то проверять; скриншот без ассерта — не тест.

---

## Чек-лист ревью спека

- [ ] Есть ли `expect` / `assert`? Если нет — тест мусорный.
- [ ] Все ли ассерты web-first (с ретраями)?
- [ ] Есть ли `waitForTimeout`, `try/catch` с подавлением, `Date.now()`-замеры?
- [ ] Используются ли только PO-методы, без `page.locator` в спеке?
- [ ] Локаторы через `data-test`, без `getByText` / `:has-text` с переменной?
- [ ] Проверяются ли итоговые суммы (subtotal/tax/total) в чекауте?
- [ ] Название теста описывает поведение, а не номер?
- [ ] Тест независим и создаёт своё состояние (не зависит от порядка)?
- [ ] Не тестируется ли функционал, которого в SauceDemo нет (API товаров, edit quantity)? Экспорт PDF есть — кнопка "Generate PDF order" на `checkout-complete.html`
- [ ] Нет хардкода таймаутов и магических чисел?
- [ ] Соответствующая правка внесена в остальные две реализации?

---

## Переходы страниц SauceDemo

- Логин: `/` → `inventory.html`
- Корзина: `inventory.html` → `cart.html`
- Чекаут: `cart.html` → `checkout-step-one.html` → `checkout-step-two.html` → `checkout-complete.html`
- `About` ведёт на `saucelabs.com` и отдаёт 403 — тест должен быть `skip` с комментарием.
- Пустая корзина: badge отсутствует, `.cart_item` — 0 элементов.
- Данные о товарах рендерятся сервером, API товаров нет.

---

## Синхронизация трёх реализаций

**Все три реализации намеренны** (практика на разных стеках + портфолио). Никогда не
предлагай удалить JS или Python, свести всё к одному языку или объявить какую-либо
реализацию устаревшей. Цель — паритет, а не сокращение числа реализаций.

При правке теста `NN-topic`:

1. Правь TS — он эталонный.
2. Отрази изменение в `js/tests/e2e/NN-topic.spec.js` и `python/tests/e2e/test-NN-topic.py`.
3. Если метод PO меняется — он меняется во всех трёх `page-objects` / `page_objects`.
4. Если реализацию осознанно не синхронизируешь — запиши расхождение в таблицу
   «Известное состояние» выше с причиной.
5. Прогони проверки из `AGENTS.md` §2 для каждой затронутой реализации.
6. Проверяй паритет набора: число файлов `NN-topic` в трёх реализациях должно совпадать
   (сейчас 29 / 29 / 29; в Python дополнительно `__init__.py`).
