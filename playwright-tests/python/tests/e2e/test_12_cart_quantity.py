"""
Тест 12: Изменение количества товара в корзине
"""
import pytest
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage
from page_objects.cart_page import CartPage


def test_cart_quantity(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.add_product_to_cart(0)
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    assert cart_page.get_cart_item_count() == 1

    product_name = cart_page.cart_items.first.locator('[data-test="inventory-item-name"]').text_content()
    assert product_name
