"""
Тест 7: Удаление товара из корзины
"""

import pytest
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
    assert inventory_page.get_cart_badge_count() == 2

    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    assert cart_page.get_cart_item_count() == 2

    cart_page.remove_item(0)
    assert cart_page.get_cart_item_count() == 1

    page.reload()
    cart_page.wait_for_page_load()
    assert inventory_page.get_cart_badge_count() == 1
