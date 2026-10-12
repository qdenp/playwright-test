"""
All users see the same catalogue
"""

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from conftest import get_all_credentials
from page_objects.inventory_page import InventoryPage


def get_test_users():
    """Return the users from env vars, falling back to the standard ones"""
    creds = get_all_credentials()
    if creds:
        return list(creds.keys())[:5]
    # Fall back to the standard users
    return [
        "standard_user",
        "problem_user",
        "performance_glitch_user",
        "error_user",
        "visual_user",
    ]


@pytest.mark.parametrize("username", get_test_users())
def test_same_catalog_for_all_users(page, username):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    # Use the standard password for the fallback users
    password = get_all_credentials().get(username, "secret_sauce")
    login_page.login(username, password)
    inventory_page.wait_for_page_load()

    # All users see 6 products
    expect(inventory_page.products).to_have_count(6)

    # Everyone sees the same product names
    names = inventory_page.get_all_product_names()
    assert len(names) == 6
    assert "Sauce Labs Backpack" in names
    assert "Sauce Labs Bike Light" in names
