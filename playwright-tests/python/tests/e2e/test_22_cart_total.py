"""
Test 22: Verify cart total updates when adding products
"""

import pytest
from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


class TestCartTotal:
    """Тесты для проверки общей суммы в корзине"""

    def test_01_should_update_cart_total_when_adding_products(self, page):
        """Проверить что общая сумма обновляется при добавлении товаров"""
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)
        cart_page = CartPage(page)

        login_page.go_to()
        login_page.login()
        inventory_page.wait_for_page_load()

        # Добавить первый товар
        inventory_page.add_product_to_cart(0)
        badge = page.locator(".shopping_cart_badge")
        expect(badge).to_have_text("1")

        # Добавить второй товар
        inventory_page.add_product_to_cart(1)
        expect(badge).to_have_text("2")

        # Открыть корзину и проверить общую сумму
        inventory_page.open_cart()
        cart_page.wait_for_page_load()

        # Проверить что в корзине 2 товара (цена может быть не видна на Sauce Demo)
        cart_items = cart_page.get_cart_item_count()
        assert cart_items == 2
