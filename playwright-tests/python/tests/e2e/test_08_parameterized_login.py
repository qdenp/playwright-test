"""
Тест 8: Параметризация логинов через pytest.mark.parametrize
"""

import pytest
from page_objects.login_page import LoginPage, get_login_params
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
        assert "inventory" in page.url
        inventory_page.wait_for_page_load()
        title = inventory_page.get_title_text()
        assert title == expected_title
    else:
        login_page.wait_for_error_message()
        error_message = login_page.get_error_message()
        assert expected_error in error_message
