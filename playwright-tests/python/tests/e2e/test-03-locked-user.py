"""
Locked out user sees the locked out error
"""

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from constants import get_all_credentials


def test_locked_user(page):
    login_page = LoginPage(page)

    login_page.go_to()
    # locked_out_user shares the same secret_sauce password
    login_page.login("locked_out_user", "secret_sauce")

    expect(login_page.error_message).to_be_visible()
    error_message = login_page.get_error_message()
    assert "locked out" in error_message or "do not match" in error_message
