"""
badge корзины растёт на 1 при добавлении и падает на 1 при удалении
"""

from playwright.sync_api import expect
from page_objects.inventory_page import InventoryPage
from page_objects.cart_page import CartPage


def test_cart_badge_increments_and_decrements(authenticated_page):
    inventory_page = authenticated_page["inventory_page"]
    page = authenticated_page["page"]
    cart_page = CartPage(page)

    # Изначально корзина пуста — badge отсутствует
    expect(inventory_page.cart_badge_count).to_have_count(0)

    # Добавление товара → badge +1
    inventory_page.add_product_to_cart(0)
    expect(inventory_page.cart_badge_count).to_have_text("1")

    # Добавление второго товара → badge ещё +1
    inventory_page.add_product_to_cart(1)
    expect(inventory_page.cart_badge_count).to_have_text("2")

    # Переходим в корзину и удаляем один товар → badge −1
    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(2)

    cart_page.remove_item(0)
    expect(cart_page.cart_items).to_have_count(1)
    expect(inventory_page.cart_badge_count).to_have_text("1")

    # Удаляем последний товар → badge пропадает
    cart_page.remove_item(0)
    expect(cart_page.cart_items).to_have_count(0)
    expect(inventory_page.cart_badge_count).to_have_count(0)
