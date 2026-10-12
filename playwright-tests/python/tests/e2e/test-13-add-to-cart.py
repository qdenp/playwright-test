"""
Adding a product to the cart
"""

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage
from page_objects.cart_page import CartPage


def test_add_to_cart(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Add the first product
    inventory_page.add_product_to_cart(0)
    expect(inventory_page.cart_badge_count).to_have_text("1")

    # Open the cart and verify the first product
    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(1)
    first_item_name = cart_page.get_cart_item_names()[0]
    assert first_item_name is not None and len(first_item_name) > 0

    # Return to the inventory page
    cart_page.continue_shopping_button.click()
    inventory_page.wait_for_page_load()

    # Add the second product
    inventory_page.add_product_to_cart(1)
    expect(inventory_page.cart_badge_count).to_have_text("2")

    # Open the cart and verify both products
    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(2)
    cart_item_names = cart_page.get_cart_item_names()
    second_item_name = cart_item_names[1]
    assert second_item_name is not None and len(second_item_name) > 0
    # The products must be different
    assert first_item_name != second_item_name
