"""
Test 29: Specific item combinations (3 of 6 products)
Test 37: Add items during page load
Test 38: Browser tab switching during checkout
"""
import pytest
from playwright.sync_api import expect
from page_objects.cart_page import CartPage


class TestItemCombinations:
    """Тесты для различных комбинаций товаров"""

    def test_01_should_add_cheapest_3_products_and_verify_total(self, authenticated_page):
        """Добавить 3 самых дешёвых товара и проверить сумму"""
        page = authenticated_page['page']
        inventory_page = authenticated_page['inventory_page']
        cart_page = CartPage(page)

        # Добавить 3 самых дешёвых: Onesie (7.99), Bike Light (9.99), Red T-Shirt (15.99)
        inventory_page.add_product_to_cart(4)  # Onesie - $7.99
        inventory_page.add_product_to_cart(1)  # Bike Light - $9.99
        inventory_page.add_product_to_cart(5)  # Red T-Shirt - $15.99

        # Проверить badge
        badge = page.locator('.shopping_cart_badge')
        expect(badge).to_have_text('3')

        # Открыть корзину и проверить сумму
        inventory_page.open_cart()
        cart_page.wait_for_page_load()

        total_price_text = cart_page.get_total_price()
        total_price = float(total_price_text.replace('$', ''))
        expected_total = 7.99 + 9.99 + 15.99

        assert total_price == expected_total

    def test_02_should_add_most_expensive_3_products_and_verify_total(self, authenticated_page):
        """Добавить 3 самых дорогих товара и проверить сумму"""
        page = authenticated_page['page']
        inventory_page = authenticated_page['inventory_page']
        cart_page = CartPage(page)

        # Добавить 3 самых дорогих: Fleece Jacket (49.99), Backpack (29.99), Bolt T-Shirt (24.99)
        inventory_page.add_product_to_cart(3)  # Fleece Jacket - $49.99
        inventory_page.add_product_to_cart(0)  # Backpack - $29.99
        inventory_page.add_product_to_cart(2)  # Bolt T-Shirt - $24.99

        # Проверить badge
        badge = page.locator('.shopping_cart_badge')
        expect(badge).to_have_text('3')

        # Открыть корзину и проверить сумму
        inventory_page.open_cart()
        cart_page.wait_for_page_load()

        total_price_text = cart_page.get_total_price()
        total_price = float(total_price_text.replace('$', ''))
        expected_total = 49.99 + 29.99 + 24.99

        assert total_price == expected_total

    def test_03_should_add_random_combination_and_verify_items_match(self, authenticated_page):
        """Добавить случайную комбинацию и проверить товары"""
        page = authenticated_page['page']
        inventory_page = authenticated_page['inventory_page']
        cart_page = CartPage(page)

        # Добавить конкретную комбинацию: Backpack, Onesie, Fleece Jacket
        inventory_page.add_product_to_cart(0)  # Backpack
        inventory_page.add_product_to_cart(4)  # Onesie
        inventory_page.add_product_to_cart(3)  # Fleece Jacket

        badge = page.locator('.shopping_cart_badge')
        expect(badge).to_have_text('3')

        # Открыть корзину
        inventory_page.open_cart()
        cart_page.wait_for_page_load()

        cart_items = cart_page.get_cart_item_names()
        assert len(cart_items) == 3

        # Проверить конкретные товары
        assert 'Sauce Labs Backpack' in cart_items
        assert 'Sauce Labs Onesie' in cart_items
        assert 'Sauce Labs Fleece Jacket' in cart_items

    def test_04_should_add_alternating_products(self, authenticated_page):
        """Добавить товары через один (1-й, 3-й, 5-й)"""
        page = authenticated_page['page']
        inventory_page = authenticated_page['inventory_page']
        cart_page = CartPage(page)

        # Добавить товары с индексами 0, 2, 4 (Backpack, Bolt T-Shirt, Onesie)
        inventory_page.add_product_to_cart(0)
        inventory_page.add_product_to_cart(2)
        inventory_page.add_product_to_cart(4)

        badge = page.locator('.shopping_cart_badge')
        expect(badge).to_have_text('3')

        inventory_page.open_cart()
        cart_page.wait_for_page_load()

        cart_items = cart_page.get_cart_item_names()
        assert 'Sauce Labs Backpack' in cart_items
        assert 'Sauce Labs Bolt T-Shirt' in cart_items
        assert 'Sauce Labs Onesie' in cart_items
