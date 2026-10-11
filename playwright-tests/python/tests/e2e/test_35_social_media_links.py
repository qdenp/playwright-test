"""
Test 35: Social Media Links
Проверка загрузки страницы после логина
"""

import pytest
from playwright.sync_api import expect
from page_objects.inventory_page import InventoryPage


class TestSocialMediaLinks:
    """Тесты для проверки загрузки страницы"""

    def test_01_should_verify_page_loads_correctly_after_login(self, page):
        """Проверить что страница загружается корректно после логина"""
        inventory_page = InventoryPage(page)

        # Логин
        page.goto("/")
        page.locator("#user-name").fill("standard_user")
        page.locator("#password").fill("secret_sauce")
        page.locator("#login-button").click()
        inventory_page.wait_for_page_load()

        # Проверить что страница товаров загрузилась корректно
        expect(inventory_page.products.first).to_be_visible()
        assert inventory_page.get_product_count() == 6

    def test_02_should_verify_page_structure_and_elements(self, page):
        """Проверить структуру страницы и элементы"""
        # Логин
        page.goto("/")
        page.locator("#user-name").fill("standard_user")
        page.locator("#password").fill("secret_sauce")
        page.locator("#login-button").click()

        inventory_page = InventoryPage(page)
        inventory_page.wait_for_page_load()

        # Проверить основные элементы страницы
        expect(page.locator(".title")).to_contain_text("Products")
        expect(inventory_page.sort_dropdown).to_be_visible()
        expect(inventory_page.cart_badge).to_be_visible()

    def test_03_should_verify_all_6_products_are_loaded(self, page):
        """Проверить что все 6 товаров загружены"""
        # Логин
        page.goto("/")
        page.locator("#user-name").fill("standard_user")
        page.locator("#password").fill("secret_sauce")
        page.locator("#login-button").click()

        inventory_page = InventoryPage(page)
        inventory_page.wait_for_page_load()

        # Проверить что все 6 товаров присутствуют
        product_count = inventory_page.get_product_count()
        assert product_count == 6

        # Проверить что все товары видны
        products = inventory_page.products
        assert products.count() == 6
