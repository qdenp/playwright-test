"""
Test 30: Add all 6 products and verify total price
Sauce Demo имеет ровно 6 товаров — добавляем все и проверяем итоговую сумму
"""

import pytest
from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.checkout_page import CheckoutPage


class TestAddAllSixProducts:
    """Тесты для добавления всех 6 товаров"""

    def test_01_should_add_all_6_products_and_verify_total_price(
        self, authenticated_page
    ):
        """Добавить все 6 товаров и проверить итоговую сумму"""
        page = authenticated_page["page"]
        inventory_page = authenticated_page["inventory_page"]
        cart_page = CartPage(page)

        # Добавить все 6 товаров (индексы 0-5)
        for i in range(6):
            inventory_page.add_product_to_cart(i)

        # Проверить badge = 6
        badge = page.locator(".shopping_cart_badge")
        expect(badge).to_have_text("6")

        # Открыть корзину
        inventory_page.open_cart()

        # Проверить что в корзине 6 товаров
        cart_page.wait_for_page_load()
        assert cart_page.get_cart_item_count() == 6

        # Проверить итоговую сумму
        # Цены: 29.99 + 9.99 + 24.99 + 49.99 + 7.99 + 15.99 = 138.94
        total_price_text = cart_page.get_total_price()
        total_price = float(total_price_text.replace("$", ""))
        expected_total = 29.99 + 9.99 + 24.99 + 49.99 + 7.99 + 15.99

        assert total_price == expected_total
        assert total_price == 138.94

    def test_02_should_verify_individual_product_prices_are_correct(
        self, authenticated_page
    ):
        """Проверить что цены всех 6 товаров правильные"""
        inventory_page = authenticated_page["inventory_page"]

        # Ожидаемые цены для всех 6 товаров
        expected_prices = {
            0: 29.99,  # Sauce Labs Backpack
            1: 9.99,  # Sauce Labs Bike Light
            2: 24.99,  # Sauce Labs Bolt T-Shirt
            3: 49.99,  # Sauce Labs Fleece Jacket
            4: 7.99,  # Sauce Labs Onesie
            5: 15.99,  # Test.allTheThings() T-Shirt (Red)
        }

        for index, expected_price in expected_prices.items():
            actual_price = inventory_page.get_product_price(index)
            assert actual_price == expected_price

    def test_03_should_add_all_6_products_and_verify_product_names(
        self, authenticated_page
    ):
        """Добавить все 6 товаров и проверить названия"""
        page = authenticated_page["page"]
        inventory_page = authenticated_page["inventory_page"]
        cart_page = CartPage(page)

        # Добавить все 6 товаров
        for i in range(6):
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

        cart_items = cart_page.get_cart_item_names()
        assert len(cart_items) == 6

        for name in expected_names:
            assert name in cart_items
