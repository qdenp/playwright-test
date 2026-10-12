"""
Неверный пароль — ошибка авторизации
"""

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_invalid_password(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login("standard_user", "wrong_password")

    expect(login_page.error_message).to_be_visible()
    error_message = login_page.get_error_message()
    assert "do not match" in error_message
