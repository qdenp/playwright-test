"""
Тест 6: Полный flow покупки (E2E)
"""

import pytest
from playwright.sync_api import expect
from constants import DEFAULT_TIMEOUT
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage
from page_objects.cart_page import CartPage
from page_objects.checkout_page import CheckoutPage


def test_full_purchase_flow(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)
    checkout_page = CheckoutPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.add_product_to_cart(0)
    inventory_page.add_product_to_cart(1)
    expect(inventory_page.cart_badge_count).to_have_text("2")

    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(2)

    cart_page.checkout()
    checkout_page.wait_for_page_load()

    checkout_page.fill_info("Иван", "Иванов", "100000")
    checkout_page.click_continue()

    # Переход на страницу сводки (checkout-step-two) и нажатие Finish
    expect(checkout_page.finish_button).to_be_visible(timeout=DEFAULT_TIMEOUT)

    # Проверяем суммы заказа: item total = сумма цен товаров
    item_prices = checkout_page.get_summary_item_prices()
    assert len(item_prices) == 2

    items_sum = sum(item_prices)
    items_total = checkout_page.get_items_total()
    assert items_total == pytest.approx(items_sum, abs=0.01)

    # tax = 8% от item total
    tax = checkout_page.get_tax()
    assert tax == pytest.approx(items_total * 0.08, abs=0.01)

    # total = item total + tax
    total = checkout_page.get_total()
    assert total == pytest.approx(items_total + tax, abs=0.01)

    checkout_page.click_finish()

    # Ожидаем страницу подтверждения
    expect(checkout_page.complete_title).to_be_visible(timeout=DEFAULT_TIMEOUT)
    expect(checkout_page.complete_title).to_contain_text("Thank you for your order")
