"""
Test 28: Specific item combinations (3 of 6 products)
"""

import pytest
from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.inventory_page import InventoryPage


def test_add_cheapest_3_products_and_verify_cart(authenticated_page):
    """Добавить 3 самых дешёвых товара и проверить корзину"""
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]
    cart_page = CartPage(page)

    # Добавить 3 самых дешёвых: Onesie (7.99), Bike Light (9.99), Red T-Shirt (15.99)
    inventory_page.add_product_to_cart(4)  # Onesie - $7.99
    inventory_page.add_product_to_cart(1)  # Bike Light - $9.99
    inventory_page.add_product_to_cart(5)  # Red T-Shirt - $15.99

    # Проверить badge
    badge = inventory_page.cart_badge_count
    expect(badge).to_have_text("3")

    # Открыть корзину и проверить товары
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Ждём отрисовки товаров, прежде чем читать список
    expect(cart_page.cart_items).to_have_count(3)

    cart_items = cart_page.get_cart_item_names()
    assert "Sauce Labs Onesie" in cart_items
    assert "Sauce Labs Bike Light" in cart_items
    assert "Test.allTheThings() T-Shirt (Red)" in cart_items


def test_add_most_expensive_3_products_and_verify_cart(authenticated_page):
    """Добавить 3 самых дорогих товара и проверить корзину"""
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]
    cart_page = CartPage(page)

    # Добавить 3 самых дорогих: Fleece Jacket (49.99), Backpack (29.99), Bolt T-Shirt (24.99)
    inventory_page.add_product_to_cart(3)  # Fleece Jacket - $49.99
    inventory_page.add_product_to_cart(0)  # Backpack - $29.99
    inventory_page.add_product_to_cart(2)  # Bolt T-Shirt - $24.99

    # Проверить badge
    badge = inventory_page.cart_badge_count
    expect(badge).to_have_text("3")

    # Открыть корзину и проверить товары
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Ждём отрисовки товаров, прежде чем читать список
    expect(cart_page.cart_items).to_have_count(3)

    cart_items = cart_page.get_cart_item_names()
    assert "Sauce Labs Fleece Jacket" in cart_items
    assert "Sauce Labs Backpack" in cart_items
    assert "Sauce Labs Bolt T-Shirt" in cart_items


def test_add_alternating_products(authenticated_page):
    """Добавить товары через один (1-й, 3-й, 5-й)"""
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]
    cart_page = CartPage(page)

    # Добавить товары с индексами 0, 2, 4 (Backpack, Bolt T-Shirt, Onesie)
    inventory_page.add_product_to_cart(0)
    inventory_page.add_product_to_cart(2)
    inventory_page.add_product_to_cart(4)

    expect(inventory_page.cart_badge_count).to_have_text("3")

    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Ждём отрисовки товаров, прежде чем читать список
    expect(cart_page.cart_items).to_have_count(3)

    cart_items = cart_page.get_cart_item_names()
    assert "Sauce Labs Backpack" in cart_items
    assert "Sauce Labs Bolt T-Shirt" in cart_items
    assert "Sauce Labs Onesie" in cart_items
