"""
Блокированный пользователь — locked_out_user
"""

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from conftest import get_all_credentials


def test_locked_user(page):
    login_page = LoginPage(page)

    login_page.go_to()
    # locked_out_user имеет тот же пароль secret_sauce
    login_page.login("locked_out_user", "secret_sauce")

    expect(login_page.error_message).to_be_visible()
    error_message = login_page.get_error_message()
    assert "locked out" in error_message or "do not match" in error_message
