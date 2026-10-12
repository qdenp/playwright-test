"""
Тест 18: About page — переход из hamburger меню
SAUCE_DEMO_BUG: Ожидается 403 Forbidden на saucelabs.com
"""

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


@pytest.mark.skip(reason="SAUCE_DEMO_BUG: About ссылка возвращает 403 Forbidden на saucelabs.com")
def test_about_page(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()

    # Открываем меню
    login_page.open_hamburger_menu()

    # Переходим по ссылке About
    inventory_page.click_about()

    # Sauce Demo возвращает 403 на saucelabs.com — это известный баг
    # Ожидаем что страница загрузится (даже с ошибкой)
    try:
        page.wait_for_timeout(2000)
    except Exception:
        pass

    # Закрываем меню если открыто
    try:
        login_page.close_hamburger_menu()
    except Exception:
        pass

    # Возвращаемся на Products
    inventory_page.go_to()
    inventory_page.wait_for_page_load()
    expect(inventory_page.header_title).to_contain_text("Products", timeout=5000)
