"""
Тест 2: Неверный пароль — ошибка авторизации
"""
import pytest
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_invalid_password(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login('standard_user', 'wrong_password')

    assert login_page.is_error_message_visible()
    error_message = login_page.get_error_message()
    assert 'do not match' in error_message
