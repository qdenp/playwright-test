"""
Removing a product from the cart
"""

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage
from page_objects.cart_page import CartPage


def test_remove_from_cart(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.add_product_to_cart(0)
    inventory_page.add_product_to_cart(1)
    expect(inventory_page.cart_badge_count).to_have_text("2")

    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(2)

    cart_page.remove_item(0)
    expect(cart_page.cart_items).to_have_count(1)

    # Go back to inventory and verify badge reflects the removal
    cart_page.continue_shopping_button.click()
    inventory_page.wait_for_page_load()
    expect(inventory_page.cart_badge_count).to_have_text("1")
