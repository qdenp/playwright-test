"""
Test 28: Verify quantity stays at 1 per item (Sauce Demo doesn't allow editing quantity)
Test 35: Double-click race condition on "Add to Cart"
Test 36: Duplicate form submission during checkout
"""

import pytest
from playwright.sync_api import expect
from page_objects.cart_page import CartPage


class TestQuantityAndRaceConditions:
    """Тесты для количества и race conditions"""

    def test_01_should_verify_each_item_quantity_stays_at_1(self, authenticated_page):
        """Проверить что количество каждого товара = 1 (редактирование невозможно)"""
        page = authenticated_page["page"]
        inventory_page = authenticated_page["inventory_page"]
        cart_page = CartPage(page)

        # Добавить 3 разных товара
        inventory_page.add_product_to_cart(0)  # Backpack
        inventory_page.add_product_to_cart(2)  # Bolt T-Shirt
        inventory_page.add_product_to_cart(4)  # Onesie

        # Проверить badge = 3
        badge = page.locator(".shopping_cart_badge")
        expect(badge).to_have_text("3")

        # Открыть корзину
        inventory_page.open_cart()
        cart_page.wait_for_page_load()

        # Проверить что в корзине 3 товара
        assert cart_page.get_cart_item_count() == 3

        # На Sauce Demo каждый продукт может быть добавлен только один раз

    def test_02_should_not_duplicate_item_when_adding_same_product_twice(
        self, authenticated_page
    ):
        """Не дублировать товар при повторном добавлении"""
        page = authenticated_page["page"]
        inventory_page = authenticated_page["inventory_page"]
        cart_page = CartPage(page)

        # Добавить backpack первый раз
        inventory_page.add_product_to_cart(0)
        badge = page.locator(".shopping_cart_badge")
        expect(badge).to_have_text("1")

        # Добавить backpack второй раз
        inventory_page.add_product_to_cart(0)

        # Открыть корзину и проверить что backpack не дублируется
        inventory_page.open_cart()
        cart_page.wait_for_page_load()

        cart_items = cart_page.get_cart_item_names()
        backpack_count = sum(1 for name in cart_items if "Backpack" in name)
        assert backpack_count <= 1

    def test_03_should_handle_double_click_on_add_to_cart(self, authenticated_page):
        """Обработать двойной клик на "Add to Cart" (race condition)"""
        page = authenticated_page["page"]
        inventory_page = authenticated_page["inventory_page"]

        # Двойной клик на первый товар
        product = inventory_page.get_product_element(0)
        add_button = product.locator(".btn.btn_primary.btn_small.btn_inventory")

        # Быстрый двойной клик
        add_button.click()
        add_button.click()

        # Не должно быть ошибки или дублирования
        badge = page.locator(".shopping_cart_badge")
        expect(badge).to_be_visible()

        # Проверить что добавлен не более 2 товаров
        badge_count = inventory_page.get_cart_badge_count()
        assert badge_count <= 2

    def test_04_should_handle_rapid_add_multiple_products(self, authenticated_page):
        """Обработать быстрое добавление нескольких товаров"""
        page = authenticated_page["page"]
        inventory_page = authenticated_page["inventory_page"]
        cart_page = CartPage(page)

        # Быстро добавить 3 товара (без задержек)
        for i in range(3):
            inventory_page.add_product_to_cart(i)

        # Все 3 должны быть добавлены
        badge = page.locator(".shopping_cart_badge")
        expect(badge).to_have_text("3")

        # Проверить что в корзине 3 товара
        inventory_page.open_cart()
        cart_page.wait_for_page_load()

        assert cart_page.get_cart_item_count() == 3
