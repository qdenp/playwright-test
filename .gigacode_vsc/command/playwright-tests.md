# Playwright Tests — Правила работы

## ⚠️ КРИТИЧЕСКИЕ ПРАВИЛА

### 1. Всегда проверяй все 3 проекта после изменений
JS, TypeScript, Python — это три одинаковых набора тестов.
**Правило:** Если меняешь что-то в одном проекте — проверяй, что это не сломало другие.

**Команды для проверки:**
```bash
# Python
cd playwright-tests/python && python -m pytest tests/e2e/ -v --tb=short

# JS
cd playwright-tests/js && npx playwright test tests/e2e/ --reporter=list

# TS
cd playwright-tests/typescript && npx playwright test tests/e2e/ --reporter=list
```

### 2. Все Page Objects должны иметь одинаковый API
Методы в JS, TS, Python PO должны называться одинаково и работать одинаково.

**Правило:** При изменении PO в одном проекте — синхронизируй с другими.

### 3. Импорты — проверяй всегда
Когда добавляешь использование `timeouts` или другого экспорта из `config` — **обязательно** добавляй импорт в файл.

**Проверка:** Перед запуском теста убедись, что все импорты есть.

---

## 📁 Структура проекта

```
playwright-tests/
├── js/
│   ├── tests/
│   │   ├── config.js          # config + timeouts
│   │   ├── fixtures.js        # authenticatedPage fixture
│   │   ├── page-objects/      # LoginPage, CartPage, InventoryPage, CheckoutPage
│   │   └── e2e/*.spec.js      # тесты
│   └── playwright.config.js
├── typescript/
│   ├── tests/
│   │   ├── config.ts          # config + timeouts
│   │   ├── fixtures.ts        # authenticatedPage fixture
│   │   ├── page-objects/      # LoginPage, CartPage, InventoryPage, CheckoutPage
│   │   └── e2e/*.spec.ts      # тесты
│   └── playwright.config.ts
└── python/
    ├── tests/
    │   ├── constants.py       # константы таймаутов
    │   ├── conftest.py        # fixtures + credentials
    │   ├── page_objects/      # LoginPage, CartPage, InventoryPage, CheckoutPage
    │   └── e2e/test-*.py      # тесты
    └── pytest.ini
```

---

## 🔧 authenticatedPage Fixture

### Что это
Fixture, который автоматически логинит пользователя и возвращает готовые PO.

### Какие тесты НЕ меняют на fixture (тестируют логин):
- `01-login` — тест успешного логина
- `02-invalid-password` — тест неверного пароля
- `03-locked-user` — тест заблокированного пользователя
- `08-parameterized-login` — параметризированный логин
- `20-multiple-users-catalog` — тест каталога для разных пользователей

### Все остальные тесты — через fixture:
```typescript
// ДО
test("Тест", async ({ page }) => {
  const loginPage = new LoginPage(page);
  const inventoryPage = new InventoryPage(page);
  await loginPage.goTo();
  await loginPage.login();
  await inventoryPage.waitForPageLoad();
  // ...
});

// ПОСЛЕ
test("Тест", async ({ authenticatedPage }) => {
  const { inventoryPage, cartPage, checkoutPage } = authenticatedPage;
  // ... (без логина)
});
```

---

## 📝 Sauce Demo — особенности

1. **Badge корзины:** когда корзина пуста — элемент `.shopping_cart_badge` полностью отсутствует
   - `getCartBadgeCount()` должен проверять `isVisible()` перед чтением текста
   - Возвращать `0` если badge не виден

2. **Пустая корзина:** `wait_for_page_load()` не должен ломаться на пустой корзине

3. **Нет API:** Sauce Demo — статический сайт, нет API вызовов

4. **Нет PDF:** кнопка "Generate PDF" отсутствует

5. **Python text_content():** может вернуть `None` — нужна проверка

---

## 🔍 Чек-лист перед коммитом

- [ ] Изменения применены ко всем 3 проектам (JS, TS, Python)
- [ ] Все импорты добавлены (особенно `timeouts` из `config`)
- [ ] Запущены тесты в Python
- [ ] Запущены тесты в JS
- [ ] Запущены тесты в TS
- [ ] Нет `ReferenceError: timeouts is not defined`
- [ ] `getCartBadgeCount()` корректно обрабатывает пустую корзину

---

## 🐛 Частые ошибки

1. **`ReferenceError: timeouts is not defined`** — забыл добавить `import { timeouts }` в файл
2. **`getCartItemNames()` возвращает пустой массив** — `waitForPageLoad()` ломается на пустой корзине
3. **Тест падает на `getCartBadgeCount()`** — badge отсутствует при пустой корзине, нужна проверка `isVisible()`
4. **Дублирование логина** — часть тестов через fixture, часть вручную. Унифицировать на fixture.
