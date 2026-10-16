"""
Adding and removing all products
"""

from playwright.sync_api import expect
from constants import EXPECTED_PRODUCT_COUNT
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage
from page_objects.cart_page import CartPage


def test_add_remove_all(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    expect(inventory_page.products).to_have_count(EXPECTED_PRODUCT_COUNT)

    # Add all products
    for i in range(EXPECTED_PRODUCT_COUNT):
        inventory_page.add_product_to_cart(i)

    expect(inventory_page.cart_badge_count).to_have_text(str(EXPECTED_PRODUCT_COUNT))

    # Go to the cart
    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(EXPECTED_PRODUCT_COUNT)

    # Remove all products
    for i in range(EXPECTED_PRODUCT_COUNT):
        cart_page.remove_item(0)
        expect(cart_page.cart_items).to_have_count(EXPECTED_PRODUCT_COUNT - 1 - i)

    # The cart is empty
    expect(cart_page.cart_items).to_have_count(0)

    page.reload()
    expect(inventory_page.cart_badge_count).to_have_count(0)
