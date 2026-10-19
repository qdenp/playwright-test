"""
Parametrise logins with pytest.mark.parametrize
"""

import pytest
from playwright.sync_api import expect
from constants import SLOW_TIMEOUT
from page_objects.login_page import LoginPage
from constants import get_login_params
from page_objects.inventory_page import InventoryPage


@pytest.mark.parametrize(
    "username,password,should_login,expected_title,expected_error",
    [get_login_params(i) for i in range(8)],
)
def test_parameterized_login(
    page, username, password, should_login, expected_title, expected_error
):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login(username, password)

    if should_login:
        # A successful login shows the catalogue.
        # performance_glitch_user needs the longer budget.
        expect(inventory_page.header_title).to_have_text(
            expected_title, timeout=SLOW_TIMEOUT
        )
    else:
        # A failed login stays on the login form and explains why
        expect(login_page.username_input).to_be_visible()
        expect(login_page.error_message).to_contain_text(expected_error)
        expect(inventory_page.header_title).to_be_hidden()
