"""
Тест 17: Logout — выход из аккаунта
"""

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_logout(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Убедимся что мы на странице товаров
    assert "inventory" in page.url

    # Открываем меню и нажимаем Logout
    login_page.open_hamburger_menu()
    inventory_page.click_logout()

    # Проверяем что попали на страницу логина (SPA — URL не меняется)
    expect(login_page.username_input).to_be_visible(timeout=10000)
    expect(login_page.password_input).to_be_visible(timeout=10000)
    expect(login_page.login_button).to_be_visible(timeout=10000)
