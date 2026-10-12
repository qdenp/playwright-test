"""
Add all 6 products and verify total price
Sauce Demo имеет ровно 6 товаров — добавляем все и проверяем итоговую сумму
"""

from playwright.sync_api import expect
from constants import EXPECTED_PRODUCT_COUNT
from page_objects.cart_page import CartPage
from page_objects.checkout_page import CheckoutPage


def test_add_all_6_products_and_verify_total_price(authenticated_page):
    """Добавить все 6 товаров и проверить итоговую сумму"""
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]
    cart_page = CartPage(page)

    # Добавить все 6 товаров (индексы 0-5)
    for i in range(EXPECTED_PRODUCT_COUNT):
        inventory_page.add_product_to_cart(i)

    # Проверить badge = 6
    expect(inventory_page.cart_badge_count).to_have_text("6", timeout=10000)

    # Открыть корзину
    inventory_page.open_cart()

    # Проверить что в корзине 6 товаров
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(6)


def test_verify_individual_product_prices_are_correct(authenticated_page):
    """Проверить что цены всех 6 товаров валидные"""
    inventory_page = authenticated_page["inventory_page"]

    expect(inventory_page.products).to_have_count(EXPECTED_PRODUCT_COUNT)

    # Проверить что все цены > 0 и <= 100
    for i in range(EXPECTED_PRODUCT_COUNT):
        price = inventory_page.get_product_price(i)
        assert price > 0
        assert price <= 100


def test_add_all_6_products_and_verify_product_names(authenticated_page):
    """Добавить все 6 товаров и проверить названия"""
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]
    cart_page = CartPage(page)

    # Добавить все 6 товаров
    for i in range(EXPECTED_PRODUCT_COUNT):
        inventory_page.add_product_to_cart(i)

    # Открыть корзину
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Проверить конкретные названия товаров
    expected_names = [
        "Sauce Labs Backpack",
        "Sauce Labs Bike Light",
        "Sauce Labs Bolt T-Shirt",
        "Sauce Labs Fleece Jacket",
        "Sauce Labs Onesie",
        "Test.allTheThings() T-Shirt (Red)",
    ]

    # Ждём отрисовки товаров, прежде чем читать список
    expect(cart_page.cart_items).to_have_count(EXPECTED_PRODUCT_COUNT)

    cart_items = cart_page.get_cart_item_names()
    assert len(cart_items) == 6

    for name in expected_names:
        assert name in cart_items
