# Ревью TS набора тестов — 2026-10-15

База: `playwright-tests/typescript/`
URL: `https://qdenp.github.io/playwright-test/`

---

## 🔴 КРИТИЧЕСКИЕ ПРОБЛЕМЫ

### 1. `27-performance-budget.spec.ts` — хардкод URL
**Файл:** `tests/e2e/27-performance-budget.spec.ts`, строка 10
**Проблема:** Жёстко захардкожен `https://www.saucedemo.com/` вместо `config.baseUrl`.
**Почему ломает:** При запуске на GitHub Pages тест идёт на неправильный URL.
**Фикс:** Заменить на `config.baseUrl`.
**✅ ИСПРАВЛЕНО:** 2026-10-15

### 2. `27-performance-budget.spec.ts` — захардкоженные бюджеты производительности
**Файл:** `tests/e2e/27-performance-budget.spec.ts`, строки 14, 27, 41, 54, 72, 87
**Проблема:** Числа `3000`, `5000`, `4000` вместо именованных констант.
**Почему:** Это не таймауты ожидания, а бизнес-бюджеты для ассертов `toBeLessThan()`. Использовать `timeouts.*` некорректно.
**Фикс:** Вынести в `performanceBudgets` в `config.ts`.
**✅ ИСПРАВЛЕНО:** 2026-10-15 — добавлен `performanceBudgets` в `config.ts`, все хардкоды заменены.

### 14. `14-remove-from-cart.spec.ts` — удалён `test.skip`
**Файл:** `tests/e2e/14-remove-from-cart.spec.ts`
**Проблема:** Тест был skipped из-за `page.reload()` после `removeItem`, который на localStorage-SPA разлогинивает пользователя.
**Фикс:** Убрать `test.skip`, заменить `page.reload()` на `cartPage.continueShopping()` — навигация в рамках SPA без перезагрузки.
**✅ ИСПРАВЛЕНО:** Тест проходит (1 passed).

### 28. `28-reset-app-state.spec.ts` — второй тест возвращён как skipped
**Файл:** `tests/e2e/28-reset-app-state.spec.ts`
**Проблема:** Второй тест проверяет "server side" сброс корзины — удаление товаров при нахождении в корзине и возврат кнопок "Add to Cart" на странице inventory. Не работает из-за бага на GitHub Pages (нужна перезагрузка, но reload разлогинивает).
**Фикс:** Вернуть тест с `test.skip` и подробным комментарием SAUCE_DEMO_BUG + данные импортировать из `config.ts`.
**✅ ИСПРАВЛЕНО:** Тест возвращён как skipped с пояснением.

### 4. `20-full-purchase-flow.spec.ts` — захардкоженная строка ассерта
**Файл:** `tests/e2e/20-full-purchase-flow.spec.ts`, строка 51
**Проблема:** Строка `"Thank you for your order"` захардкожена.
**Фикс:** Вынести в `COMPLETE_ORDER_TEXT` в `config.ts`.
**✅ ИСПРАВЛЕНО:** 2026-10-15 — добавлена `COMPLETE_ORDER_TEXT` в `config.ts`, тест импортирует из `config`.

---

## 🟡 СРЕДНИЕ ПРОБЛЕМЫ

### 5. `25-responsive-design.spec.ts` — захардкоженный таймаут
**Файл:** `tests/e2e/25-responsive-design.spec.ts`, строка 70
**Проблема:** `timeout: 10000` вместо `timeouts.DEFAULT`.
**✅ ИСПРАВЛЕНО:** 2026-10-15 — добавлен импорт `timeouts`, хардкод заменён.

### 6. `InventoryPage.ts` — хардкод таймаута в waitForPageLoad
**Файл:** `tests/page-objects/InventoryPage.ts`, строка 60
**Проблема:** `timeout: 10000` вместо `timeouts.DEFAULT`.
**✅ ИСПРАВЛЕНО:** 2026-10-15 — заменено на `timeouts.DEFAULT`.

### 7. ~~`InventoryPage.ts` — дублирующие методы гамбургер-меню~~
**Решено не исправлять:** гамбургер-меню есть на login и inventory страницах, методы используются в тестах (`06-logout-from-pages`, `28-reset-app-state`). Это допустимое дублирование.

### 8. `InventoryPage.ts` — два локатора для одной кнопки
**Файл:** `tests/page-objects/InventoryPage.ts`, строки 45 и 73
**Проблема:** Один и тот же селектор `.btn.btn_primary.btn_small.btn_inventory` используется как публичный локатор и внутри метода `addProductToCart`.
**Фикс:** Метод использует `this.addToCartButtons` вместо повторения селектора.
**✅ ИСПРАВЛЕНО:** 2026-10-15 — `addProductToCart` использует `product.locator(this.addToCartButtons)`.

### 9. `InventoryPage.ts` — `addProductByName()` с `:has-text()`
**Файл:** `tests/page-objects/InventoryPage.ts`, строки 76-81 (удалено)
**Проблема:** Метод использовал `.inventory_item:has-text("${productName}")` — нарушает AGENTS.md п.3.5. Метод нигде не используется в тестах.
**Фикс:** Удалить как мёртвый код (AGENTS.md п.3.4).
**✅ ИСПРАВЛЕНО:** 2026-10-15 — метод удалён.

### 10. Непоследовательные импорты
**Проблема:** Микс `from "../fixtures"`, `from "../fixtures.js"`, `from '../fixtures.js'`.
**Фикс:** Привести к единому стилю (без `.js` для `moduleResolution: "node"`, двойные кавычки).
**✅ ИСПРАВЛЕНО:** 2026-10-15 — все 29 тестовых файлов + `fixtures.ts` приведены к `from "../fixtures"`, `from "../config"`, `from "../page-objects/..."`.

---

## 📋 ЧЕК-ЛИСТЫ

### Page Objects — соответствие страницам
| PO | Страница | Соответствие |
|---|---|---|
| `LoginPage` | `/` (login) | ✅ |
| `InventoryPage` | `/inventory.html` | ✅ |
| `CartPage` | `/cart.html` | ✅ |
| `CheckoutPage` | `/checkout-step-one.html`, `/checkout-step-two.html`, `/checkout-complete.html` | ✅ |

### Временные файлы
- `node_modules/`, `playwright-report/`, `test-results/`, `dist/` — не коммитятся ✅
- Временных файлов (`.tmp`, `.bak`, `__snapshots__`) не обнаружено ✅

### TypeScript-компиляция
- `strict: true`, `moduleResolution: "node"`, `"type": "module"` ✅
- Импорт с `.js` суффиксом — корректно для ESM ✅
