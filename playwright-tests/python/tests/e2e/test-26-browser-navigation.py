"""
Browser navigation tests
"""

import re

import pytest
from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.checkout_page import CheckoutPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_handle_browser_back_button(page):
    """Verify the back button from cart to inventory"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.add_product_to_cart(0)
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Navigate back to the inventory page
    page.go_back()
    inventory_page.wait_for_page_load()

    expect(page).to_have_url(re.compile(r".*inventory"))
    expect(inventory_page.header_title).to_contain_text("Products")


def test_handle_browser_forward_button(page):
    """Verify the forward button after the back button"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.add_product_to_cart(0)
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Back
    page.go_back()
    inventory_page.wait_for_page_load()

    # Forward
    page.go_forward()
    cart_page.wait_for_page_load()

    expect(page).to_have_url(re.compile(r".*cart"))


def test_handle_back_from_checkout_to_cart(page):
    """Verify returning from checkout to the cart"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)
    checkout_page = CheckoutPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.add_product_to_cart(0)
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    cart_page.checkout()
    checkout_page.wait_for_page_load()

    # Return to the cart
    page.go_back()
    cart_page.wait_for_page_load()

    expect(page).to_have_url(re.compile(r".*cart"))


def test_preserve_cart_state_after_navigation(page):
    """Verify the cart keeps its state after back/forward navigation"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.add_product_to_cart(0)
    inventory_page.add_product_to_cart(1)
    badge = inventory_page.cart_badge_count
    expect(badge).to_have_text("2")

    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Back and forward navigation
    page.go_back()
    inventory_page.wait_for_page_load()
    page.go_forward()
    cart_page.wait_for_page_load()

    # The cart must retain 2 products
    expect(badge).to_have_text("2")
