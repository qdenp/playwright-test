"""
Specific item combinations (3 of 6 products)
"""

from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def _open_cart_with_three(page, indices):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Read the catalog prices before leaving the inventory page
    catalog_prices = inventory_page.get_all_product_prices()
    return inventory_page, cart_page, catalog_prices


def test_add_cheapest_3_products_and_verify_cart(page):
    inventory_page, cart_page, catalog_prices = _open_cart_with_three(page, [4, 1, 5])
    cheapest_three = sorted(catalog_prices)[:3]

    # Add 3 cheapest: Onesie (7.99), Bike Light (9.99), Red T-Shirt (15.99)
    inventory_page.add_product_to_cart(4)
    inventory_page.add_product_to_cart(1)
    inventory_page.add_product_to_cart(5)

    expect(inventory_page.cart_badge_count).to_have_text("3")

    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(3)

    cart_items = cart_page.get_cart_item_names()
    assert "Sauce Labs Onesie" in cart_items
    assert "Sauce Labs Bike Light" in cart_items
    assert "Test.allTheThings() T-Shirt (Red)" in cart_items

    # The three cheapest in the catalogue are the three items in the cart
    prices_in_cart = sorted(cart_page.get_cart_item_prices())
    assert prices_in_cart == cheapest_three


def test_add_most_expensive_3_products_and_verify_cart(page):
    inventory_page, cart_page, catalog_prices = _open_cart_with_three(page, [3, 0, 2])
    priciest_three = sorted(catalog_prices, reverse=True)[:3]

    # Add 3 most expensive: Fleece Jacket (49.99), Backpack (29.99), Bolt T-Shirt (24.99)
    inventory_page.add_product_to_cart(3)
    inventory_page.add_product_to_cart(0)
    inventory_page.add_product_to_cart(2)

    expect(inventory_page.cart_badge_count).to_have_text("3")

    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(3)

    cart_items = cart_page.get_cart_item_names()
    assert "Sauce Labs Fleece Jacket" in cart_items
    assert "Sauce Labs Backpack" in cart_items
    assert "Sauce Labs Bolt T-Shirt" in cart_items

    prices_in_cart = sorted(cart_page.get_cart_item_prices(), reverse=True)
    assert prices_in_cart == priciest_three


def test_add_alternating_products_1st_3rd_5th(page):
    inventory_page, cart_page, catalog_prices = _open_cart_with_three(page, [0, 2, 4])
    alternating = sorted([catalog_prices[0], catalog_prices[2], catalog_prices[4]])

    # Add items at indices 0, 2, 4 (Backpack, Bolt T-Shirt, Onesie)
    inventory_page.add_product_to_cart(0)
    inventory_page.add_product_to_cart(2)
    inventory_page.add_product_to_cart(4)

    expect(inventory_page.cart_badge_count).to_have_text("3")

    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(3)

    cart_items = cart_page.get_cart_item_names()
    assert "Sauce Labs Backpack" in cart_items
    assert "Sauce Labs Bolt T-Shirt" in cart_items
    assert "Sauce Labs Onesie" in cart_items

    # Alternating means the odd-indexed catalog entries were picked
    prices_in_cart = sorted(cart_page.get_cart_item_prices())
    assert prices_in_cart == alternating
