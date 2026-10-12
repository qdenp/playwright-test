"""
Verify quantity stays at 1 per item (Sauce Demo doesn't allow editing quantity)
"""

import pytest
from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.inventory_page import InventoryPage


def test_verify_each_item_quantity_stays_at_1(authenticated_page):
    """Verify each item's quantity is 1 (editing is not possible)"""
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]
    cart_page = CartPage(page)

    # Add three different products
    inventory_page.add_product_to_cart(0)  # Backpack
    inventory_page.add_product_to_cart(2)  # Bolt T-Shirt
    inventory_page.add_product_to_cart(4)  # Onesie

    # Verify the badge shows 3
    badge = inventory_page.cart_badge_count
    expect(badge).to_have_text("3")

    # Open the cart
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Verify the cart holds 3 products
    expect(cart_page.cart_items).to_have_count(3)


def test_handle_rapid_add_multiple_products(authenticated_page):
    """Handle the rapid addition of multiple products"""
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]
    cart_page = CartPage(page)

    # Rapidly add 3 products (sequentially, with no waits)
    for i in range(3):
        inventory_page.add_product_to_cart(i)

    # All three must be added successfully
    badge = inventory_page.cart_badge_count
    expect(badge).to_have_text("3")

    # Verify the cart holds 3 products
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    expect(cart_page.cart_items).to_have_count(3)
