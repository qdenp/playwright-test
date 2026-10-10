"""
Тест 13: Добавление и удаление всех товаров
"""
import pytest
from tests.page_objects.login_page import LoginPage
from tests.page_objects.inventory_page import InventoryPage
from tests.page_objects.cart_page import CartPage


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

    assert inventory_page.get_cart_badge_count() == product_count

    # Переходим в корзину
    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    assert cart_page.get_cart_item_count() == product_count

    # Удаляем все товары
    for i in range(product_count):
        first_item = page.locator('.cart_item').first
        first_item.get_by_role('button', name='Remove').click()
        assert page.locator('.cart_item').count() == product_count - 1 - i

    # Корзина пуста
    assert cart_page.get_cart_item_count() == 0

    page.reload()
    assert not page.locator('.shopping_cart_badge').is_visible()
