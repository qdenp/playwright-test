"""
Тест 18: About page — переход из hamburger меню

SAUCE_DEMO_BUG: ссылка About ведёт на saucelabs.com, который отдаёт
403 Forbidden. Проверить переход и содержимое страницы About невозможно,
поэтому тест помечен как skip.
"""

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


@pytest.mark.skip(reason="SAUCE_DEMO_BUG: About ведёт на saucelabs.com, который отдаёт 403 Forbidden")
def test_about_page(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()

    # Открываем hamburger меню и переходим по ссылке About
    login_page.open_hamburger_menu()
    inventory_page.click_about()

    # Переход проверить нельзя: saucelabs.com отдаёт 403 Forbidden.
    # Возвращаемся на Products и убеждаемся, что приложение живо.
    inventory_page.go_to()
    inventory_page.wait_for_page_load()
    expect(inventory_page.header_title).to_contain_text("Products", timeout=5000)
