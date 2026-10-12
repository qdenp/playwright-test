"""
Тест 6: Полный flow покупки (E2E)
"""

import pytest
from playwright.sync_api import expect
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
    expect(page.locator(".shopping_cart_badge")).to_have_text("2")

    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(2)

    cart_page.checkout()
    checkout_page.wait_for_page_load()

    checkout_page.fill_info("Иван", "Иванов", "100000")
    checkout_page.click_continue()

    # Переход на страницу сводки (checkout-step-two) и нажатие Finish
    checkout_page.finish_button.wait_for(state="visible", timeout=10000)
    checkout_page.click_finish()

    # Ожидаем страницу подтверждения
    expect(checkout_page.thank_you_message).to_be_visible()
