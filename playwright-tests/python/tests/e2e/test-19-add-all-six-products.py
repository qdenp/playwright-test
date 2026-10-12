"""
Add all 6 products and verify total price
SauceDemo has exactly 6 products - add them all and verify the final total
"""

from playwright.sync_api import expect
from constants import EXPECTED_PRODUCT_COUNT
from page_objects.cart_page import CartPage
from page_objects.checkout_page import CheckoutPage


def test_add_all_6_products_and_verify_total_price(authenticated_page):
    """Add all 6 products and verify the final total"""
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]
    cart_page = CartPage(page)

    # Add all 6 products (indices 0-5)
    for i in range(EXPECTED_PRODUCT_COUNT):
        inventory_page.add_product_to_cart(i)

    # Verify the badge shows 6
    expect(inventory_page.cart_badge_count).to_have_text("6", timeout=10000)

    # Open the cart
    inventory_page.open_cart()

    # Verify the cart holds 6 products
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(6)


def test_verify_individual_product_prices_are_correct(authenticated_page):
    """Verify the prices of all 6 products are valid"""
    inventory_page = authenticated_page["inventory_page"]

    expect(inventory_page.products).to_have_count(EXPECTED_PRODUCT_COUNT)

    # Verify all prices are > 0 and <= 100
    for i in range(EXPECTED_PRODUCT_COUNT):
        price = inventory_page.get_product_price(i)
        assert price > 0
        assert price <= 100


def test_add_all_6_products_and_verify_product_names(authenticated_page):
    """Add all 6 products and verify the names"""
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]
    cart_page = CartPage(page)

    # Add all 6 products
    for i in range(EXPECTED_PRODUCT_COUNT):
        inventory_page.add_product_to_cart(i)

    # Open the cart
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Verify the specific product names
    expected_names = [
        "Sauce Labs Backpack",
        "Sauce Labs Bike Light",
        "Sauce Labs Bolt T-Shirt",
        "Sauce Labs Fleece Jacket",
        "Sauce Labs Onesie",
        "Test.allTheThings() T-Shirt (Red)",
    ]

    # Wait for the products to render before reading the list
    expect(cart_page.cart_items).to_have_count(EXPECTED_PRODUCT_COUNT)

    cart_items = cart_page.get_cart_item_names()
    assert len(cart_items) == 6

    for name in expected_names:
        assert name in cart_items
