"""
Fixtures for pytest-playwright
"""

import sys
import os

# Add tests to PYTHONPATH so imports resolve
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

import pytest

# Re-export test data so existing imports still work
from constants import get_all_credentials, get_login_params


@pytest.fixture
def authenticated_page(page):
    """Automatically authenticated page"""
    from page_objects.login_page import LoginPage
    from page_objects.inventory_page import InventoryPage
    from page_objects.cart_page import CartPage
    from page_objects.checkout_page import CheckoutPage

    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)
    checkout_page = CheckoutPage(page)

    login_page.go_to()
    login_page.login()  # With no arguments - uses the credentials from the env vars
    inventory_page.wait_for_page_load()

    return {
        "page": page,
        "login_page": login_page,
        "inventory_page": inventory_page,
        "cart_page": cart_page,
        "checkout_page": checkout_page,
    }
