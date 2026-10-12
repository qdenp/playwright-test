"""
Verify logout functionality from different pages
"""

import pytest
from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_logout_from_inventory_and_cart_pages(page):
    """Verify signing out from the inventory and cart pages"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    # Log in
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Sign out from the inventory page
    inventory_page.open_hamburger_menu()
    inventory_page.click_logout()
    # SauceDemo redirects to the login page after logout
    login_page.username_input.wait_for(state="visible", timeout=10000)

    # Log in again
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Add a product and go to the cart
    inventory_page.add_product_to_cart(0)
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Open the menu and sign out
    inventory_page.open_hamburger_menu()
    inventory_page.click_logout()
    # SauceDemo redirects to the login page
    login_page.username_input.wait_for(state="visible", timeout=10000)
