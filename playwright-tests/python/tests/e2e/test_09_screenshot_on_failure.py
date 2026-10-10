"""
Тест 9: Скриншот при падении — неверные данные
"""
import pytest
from tests.page_objects.login_page import LoginPage


def test_screenshot_on_failure(page):
    login_page = LoginPage(page)

    login_page.go_to()
    login_page.login('standard_user', 'wrong_password')

    assert login_page.is_error_message_visible()
    error_message = login_page.get_error_message()
    assert 'do not match' in error_message
