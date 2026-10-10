"""
Тест 6: Полный flow покупки (E2E)
"""
import pytest
from tests.page_objects.login_page import LoginPage
from tests.page_objects.inventory_page import InventoryPage
from tests.page_objects.cart_page import CartPage
from tests.page_objects.checkout_page import CheckoutPage


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
    assert inventory_page.get_cart_badge_count() == 2

    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    assert cart_page.get_cart_item_count() == 2

    cart_page.checkout()
    checkout_page.wait_for_page_load()

    checkout_page.fill_info('Иван', 'Иванов', '100000')
    checkout_page.click_continue()

    # Переход на страницу сводки (checkout-step-two) и нажатие Finish
    checkout_page.finish_button.wait_for(state='visible', timeout=10000)
    checkout_page.click_finish()

    # Ожидаем страницу подтверждения
    assert checkout_page.is_complete_header_visible()
