# Холодная ревью тестов Playwright

**Дата:** 2025-01-2026
**Охват:** JS (35 файлов), TypeScript (35 файлов), Python (35 файлов)

---

## 🔥 КРИТИЧЕСКИЕ ПРОБЛЕМЫ (P0)

### 1. Дублирование тестов

#### Пара 1: test_01_login + test_10_cross_browser
**Что дублируется:**
- login → assert URL → assert title → assert products visible
- Оба теста проверяют успешный вход standard_user

**Рекомендация:**
- Удалить `test_10_cross_browser` (или переименовать в проверку кросс-браузерности)
- Оставить `test_01_login` как базовый тест логина

#### Пара 2: test_02_invalid_password + test_09_screenshot_on_failure
**Что дублируется:**
- login с wrong password → assert error message
- test_09 НЕ делает скриншотов, просто проверяет ошибку логина

**Рекомендация:**
- Удалить `test_09_screenshot_on_failure` полностью
- Название вводит в заблуждение (обещает скриншот, не делает)

#### Пара 3: test_04_add_to_cart + test_22_cart_total
**Что дублируется:**
- login → add 2 products → check badge count
- test_22 обещает проверку "total price", но проверяет только badge

**Рекомендация:**
- Удалить `test_22_cart_total`
- Либо переписать test_22 чтобы он реально проверял total price в корзине

#### Пара 4: test_07_remove_from_cart + test_13_add_remove_all
**Что дублируется:**
- login → add 2 products → open cart → remove → verify empty
- test_13 проверяет удаление ВСЕХ товаров (дублирует логику test_07)

**Рекомендация:**
- Удалить `test_13_add_remove_all`
- Либо переписать чтобы test_13 проверял удаление по одному с разными стратегиями

#### Пара 5: test_17_logout + test_26_logout_from_pages
**Что дублируется:**
- login → open menu → click logout → verify login page
- test_26 проверяет logout с 2 страниц (inventory + cart), test_17 только с inventory

**Рекомендация:**
- Удалить `test_17_logout`
- test_26 уже покрывает test_17 + добавляет проверку cart

#### Пара 6: test_19_item_detail + test_25_inventory_details
**Что дублируется:**
- login → check product name, description, price, add to cart button
- Оба проверяют детали товаров на странице inventory

**Рекомендация:**
- Удалить `test_19_item_detail`
- test_25 проверяет ВСЕ 6 товаров с полной проверкой

#### Пара 7: test_15_hamburger_menu + test_24_sidebar
**Что дублируется:**
- login → open hamburger menu → check menu items visible
- Оба проверяют элементы бокового меню

**Рекомендация:**
- Удалить `test_24_sidebar`
- test_15 уже проверяет открытие/закрытие меню

#### Пара 8: test_06_full_purchase + test_21_export_pdf
**Что дублируется:**
- login → add 2 products → checkout → fill info → complete
- test_21 тестирует кнопку "Generate PDF" которой НЕТ в Sauce Demo

**Рекомендация:**
- Удалить `test_21_export_pdf` полностью (тестирует несуществующий функционал)
- test_06 покрывает полный flow покупки

---

### 2. Тесты, которые не тестируют то, что заявлено

#### test_09_screenshot_on_failure.py
**Обещание:** Скриншот при падении теста
**Реальность:** Просто проверяет wrong password error message
**Проблема:** Нет кода для создания скриншотов, нет assertions на screenshot
**Решение:** Удалить (дубликат test_02)

#### test_21_export_pdf.py
**Обещание:** Экспорт PDF заказа
**Реальность:** Тестирует кнопку "Generate PDF" которой нет в Sauce Demo
**Проблема:** Тест всегда падает на Sauce Demo
**Решение:** Удалить

#### test_35_social_media_links.py
**Обещание:** Проверка социальных ссылок (X, Facebook, LinkedIn)
**Реальность:** Проверяет загрузку страницы после логина и количество товаров
**Проблема:** Не проверяет ни одну социальную ссылку
**Решение:** Переименовать в `test_35_page_load` или удалить

#### test_30_api_intercept.py
**Обещание:** Перехват API ответов
**Реальность:** Sauce Demo - статический сайт, нет API вызовов
**Проблема:** Тесты бессмысленны для статического сайта
**Решение:** Удалить или переписать в проверку загрузки страницы

---

### 3. Прямой доступ к DOM вместо Page Objects (10+ тестов)

Тесты, которые обходят Page Objects и используют `page.locator()` напрямую:

| Файл | Проблема |
|------|----------|
| test_07 | `page.locator('[data-test="inventory-item"]')` вместо `cartPage.cartItems` |
| test_11 | `page.locator('.error-message-container')` напрямую |
| test_13 | `page.locator('.cart_item').first` напрямую |
| test_15 | `page.locator('[data-test="inventory-sidebar-link"]')` - такого селектора нет |
| test_16 | `page.locator('.title')` напрямую |
| test_17 | `page.get_by_text("Logout")` вместо `login_page.click_logout()` |
| test_18 | `page.get_by_text("About")` напрямую |
| test_19 | Весь тест без Page Objects (15+ прямых локаторов) |
| test_26 | `page.locator('.bm-item-list a:has-text("Logout")')` напрямую |
| test_35 | Весь тест - прямой доступ к `#user-name`, `#password`, `#login-button` |

**Рекомендация:** Заменить все прямые локаторы на методы Page Objects.

---

## ⚠️ СРЕДНИЕ ПРОБЛЕМЫ (P1)

### 4. Непоследовательности в стиле

#### Стиль именования тестов
| Стиль | Файлы |
|-------|-------|
| `class TestX: def test_XX_...()` | test_22 - test_35 (14 файлов) |
| `def test_XX_...()` | test_01 - test_21 (21 файл) |

#### Стиль логина
| Стиль | Файлы |
|-------|-------|
| `LoginPage(page).go_to().login()` | test_01 - test_34 (34 файла) |
| Прямой `page.goto('/').locator(...).fill(...)` | test_35 (1 файл) |

#### Стиль ассершн
| Стиль | Файлы |
|-------|-------|
| Только `assert` | test_01, test_04, test_05, test_07, test_12, test_13, test_14, test_20, test_25, test_29 |
| `assert` + `expect()` | test_06, test_17, test_19, test_21, test_22, test_23, test_24, test_26, test_27, test_28, test_30, test_31, test_32, test_33, test_34, test_35 |
| Только `expect()` | test_08, test_10, test_11, test_15, test_16, test_18 |

---

### 5. Хардкод магических чисел

| Значение | Где используется | Рекомендация |
|----------|------------------|--------------|
| `product_count == 6` | test_05, test_14, test_19, test_20, test_23, test_25, test_29, test_30, test_35 | `EXPECTED_PRODUCTS_COUNT = 6` |
| `timeout: 10000` | 20+ мест в PO и тестах | `DEFAULT_TIMEOUT = 10000` |
| `timeout: 60000` | LoginPage.go_to(), test_06, test_08 | `NAVIGATION_TIMEOUT = 30000` |
| `timeout: 3000` | wait_for_error_message, test_18 | `ERROR_TIMEOUT = 3000` |
| `timeout: 5000` | expect() вызовы, test_19 | `ASSERT_TIMEOUT = 5000` |
| `https://www.saucedemo.com` | login_page.py, test_18, test_30, test_33 | Использовать `page.base_url` |

---

### 6. Проблемы с Page Objects

#### LoginPage.py
- **Проблема:** Бизнес-логика (`get_all_credentials()`, `get_login_params()`) в PO
- **Решение:** Вынести в `conftest.py` или `credentials.py`
- **Проблема:** `click_logout()` использует `get_by_role()`, а тесты используют `get_by_text()`
- **Решение:** Унифицировать локатор

#### CartPage.py
- **Проблема:** `import re` внутри метода `get_cart_item_quantity()`
- **Решение:** Переместить import на уровень модуля
- **Проблема:** `clear_cart()` использует try/catch который скрывает ошибки
- **Решение:** Убрать silent catch
- **Проблема:** `wait_for_page_load()` предполагает наличие элементов
- **Решение:** Проверить на пустую корзину

#### CheckoutPage.py
- **Проблема:** `checkout()` и `click_continue()` - один и тот же метод
- **Решение:** Удалить дубликат или переименовать
- **Проблема:** `is_error_message_visible()` проверяет `count() > 0` вместо `is_visible()`
- **Решение:** Использовать `is_visible()`

---

## 💡 НИЗКОПРИОРИТЕТНЫЕ ПРОБЛЕМЫ (P2)

### 7. Неиспользуемые методы в Page Objects

#### CartPage.js/TS/Python
- `changeItemQuantity()` - не вызывается
- `continueShopping()` - не вызывается
- `getTotalPrice()` - не вызывается

#### CheckoutPage.js/TS/Python
- `getOrderSummary()` - не вызывается
- `getFirstNameValue()` - не вызывается
- `getLastNameValue()` - не вызывается
- `getZipCodeValue()` - не вызывается
- `itemsInOrder`, `itemPrices`, `itemTotals`, `backToHomeButton` - неиспользуемые локаторы

#### InventoryPage.js
- `addProductByName()` - не вызывается
- `openHamburgerMenu()`, `clickResetAppState()`, `clickLogout()`, `clickBackToProducts()` - дублируют LoginPage

### 8. Неиспользуемые импорты в тестах

| Файл | Неиспользуемый импорт |
|------|----------------------|
| test_01_login | `from page_objects.cart_page import CartPage` |
| test_03_locked_user | `from page_objects.inventory_page import InventoryPage` |
| test_17_logout | `from page_objects.inventory_page import InventoryPage` (используется, но избыточно) |
| test_18_about_page | `from page_objects.inventory_page import InventoryPage` |
| test_23_product_images | `from page_objects.cart_page import CartPage` |
| test_24_sidebar | `from page_objects.cart_page import CartPage` |
| test_26_logout_from_pages | `from page_objects.checkout_page import CheckoutPage` |
| test_27_quantity_race_condition | `from page_objects.checkout_page import CheckoutPage` |
| test_28_item_combinations | `from page_objects.checkout_page import CheckoutPage` |
| test_29_add_all_six_products | `from page_objects.checkout_page import CheckoutPage` |
| test_30_api_intercept | `from page_objects.checkout_page import CheckoutPage` |
| test_31_responsive_design | `from page_objects.checkout_page import CheckoutPage` |
| test_32_browser_navigation | `from page_objects.checkout_page import CheckoutPage` |

### 9. Антипаттерны

| Файл | Антипаттерн |
|------|-------------|
| test_18_about_page | `page.wait_for_timeout(2000)` - фиксированная задержка |
| test_33_performance_budget | `__import__('time').time()` вместо `import time` |
| test_18_about_page | `try/catch` вокруг `expect` - тест всегда проходит |

---

## 📊 СТАТИСТИКА

### Общий обзор
| Метрика | Значение |
|---------|----------|
| Всего e2e файлов | 35 (JS) + 35 (TS) + 35 (Python) = 105 |
| Файлов с критическими проблемами | ~20 |
| Файлов со средними проблемами | ~30 |
| Дублирующихся тестов | 8 пар = 16 файлов |
| Тестов с прямыми локаторами (без PO) | 10+ |
| Неиспользуемых методов в PO | 11+ |
| Неиспользуемых локаторов в PO | 6+ |
| Хардкод магических чисел | 12+ случаев |
| Неунифицированных импортов | 2+ варианта |
| Неунифицированных локаторов | 3+ варианта для одного элемента |

### Потенциал оптимизации
- После удаления дубликатов: 35 → 25 тестов (-30%)
- После удаления нерабочих тестов: 25 → 21 тест (-15%)
- **Итого: 35 → 21 тест (-40%)**

---

## 🎯 ПЛАН ИСПРАВЛЕНИЯ

### Фаза 1: Удаление (P0)
1. Удалить test_09_screenshot_on_failure (дубликат test_02)
2. Удалить test_21_export_pdf (несуществующий функционал)
3. Удалить test_30_api_intercept (бессмыслен для статического сайта)
4. Переименовать test_35_social_media_links → test_35_page_load

### Фаза 2: Объединение (P1)
5. Объединить test_01 + test_10 → один тест с параметризацией
6. Объединить test_04 + test_22 → один тест
7. Объединить test_07 + test_13 → один тест
8. Объединить test_17 + test_26 → один тест
9. Объединить test_19 + test_25 → один тест
10. Объединить test_15 + test_24 → один тест

### Фаза 3: Унификация (P1)
11. Заменить прямые локаторы на Page Objects методы
12. Унифицировать стиль логина (все через LoginPage)
13. Унифицировать стиль ассершн (все expect или все assert)
14. Ввести константы для таймаутов и product_count

### Фаза 4: Очистка (P2)
15. Удалить неиспользуемые методы из Page Objects
16. Удалить неиспользуемые импорты из тестов
17. Убрать антипаттерны (wait_for_timeout, try/catch around expect)
18. Унифицировать стили именования

---

## 📝 ПРИМЕЧАНИЯ

### Sauce Demo особенности
- Статический сайт, нет API вызовов
- Redirects to login после Reset App State
- Empty cart не имеет badge элемента
- `getByRole('menuitem')` не работает для меню, использовать `getByText()`
- Python `text_content()` может вернуть None - нужна проверка

### Recommended test structure
```python
# Style: class-based для тестов с >1 методом
class TestFeatureName:
    """Docstring на русском"""
    
    def test_01_should_do_something(self, authenticated_page):
        """Docstring на русском"""
        # Arrange
        page = authenticated_page["page"]
        inventory_page = authenticated_page["inventory_page"]
        
        # Act
        inventory_page.add_product_to_cart(0)
        
        # Assert
        expect(page.locator(".shopping_cart_badge")).to_have_text("1")
```

### Constants to add
```python
# constants.py
EXPECTED_PRODUCTS_COUNT = 6
DEFAULT_TIMEOUT = 10000
NAVIGATION_TIMEOUT = 30000
ERROR_TIMEOUT = 3000
ASSERT_TIMEOUT = 5000
```
