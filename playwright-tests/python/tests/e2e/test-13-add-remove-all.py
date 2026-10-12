"""
Тест 13: Добавление и удаление всех товаров
"""

import pytest
from playwright.sync_api import expect
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

    product_count = inventory_page.get_product_count()

    # Добавляем все товары
    for i in range(product_count):
        inventory_page.add_product_to_cart(i)

    expect(page.locator(".shopping_cart_badge")).to_have_text(str(product_count))

    # Переходим в корзину
    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(product_count)

    # Удаляем все товары
    for i in range(product_count):
        cart_page.remove_item(0)
        expect(cart_page.cart_items).to_have_count(product_count - 1 - i)

    # Корзина пуста
    expect(cart_page.cart_items).to_have_count(0)

    page.reload()
    expect(page.locator(".shopping_cart_badge")).not_to_be_visible()
