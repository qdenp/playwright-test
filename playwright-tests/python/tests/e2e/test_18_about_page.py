"""
Тест 18: About page — переход из hamburger меню
"""

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_about_page(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()

    # Открываем меню
    login_page.open_hamburger_menu()

    # Переходим по ссылке About
    inventory_page.click_about()

    # Закрываем меню если открыто
    try:
        login_page.close_hamburger_menu()
    except Exception:
        pass

    # Возвращаемся на Products
    inventory_page.go_to()
    inventory_page.wait_for_page_load()
    expect(inventory_page.header_title).to_contain_text("Products", timeout=5000)
