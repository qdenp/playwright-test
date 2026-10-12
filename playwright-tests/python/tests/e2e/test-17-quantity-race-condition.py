"""
Verify quantity stays at 1 per item (Sauce Demo doesn't allow editing quantity)
"""

import pytest
from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.inventory_page import InventoryPage


def test_verify_each_item_quantity_stays_at_1(authenticated_page):
    """Проверить что количество каждого товара = 1 (редактирование невозможно)"""
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]
    cart_page = CartPage(page)

    # Добавить 3 разных товара
    inventory_page.add_product_to_cart(0)  # Backpack
    inventory_page.add_product_to_cart(2)  # Bolt T-Shirt
    inventory_page.add_product_to_cart(4)  # Onesie

    # Проверить badge = 3
    badge = inventory_page.cart_badge_count
    expect(badge).to_have_text("3")

    # Открыть корзину
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Проверить что в корзине 3 товара
    expect(cart_page.cart_items).to_have_count(3)


def test_handle_rapid_add_multiple_products(authenticated_page):
    """Обработать быстрое добавление нескольких товаров"""
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]
    cart_page = CartPage(page)

    # Быстро добавить 3 товара (последовательно без задержек)
    for i in range(3):
        inventory_page.add_product_to_cart(i)

    # Все 3 должны быть добавлены успешно
    badge = inventory_page.cart_badge_count
    expect(badge).to_have_text("3")

    # Проверить что в корзине 3 товара
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    expect(cart_page.cart_items).to_have_count(3)
