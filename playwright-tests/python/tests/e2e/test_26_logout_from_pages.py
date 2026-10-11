"""
Test 26: Verify logout functionality from different pages
"""

import pytest
from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


class TestLogoutFromPages:
    """Тесты для проверки выхода с разных страниц"""

    def test_01_should_logout_from_inventory_and_cart_pages(self, page):
        """Проверить выход из аккаунта со страницы товаров и корзины"""
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)
        cart_page = CartPage(page)

        # Войти в систему
        login_page.go_to()
        login_page.login()
        inventory_page.wait_for_page_load()

        # Выйти из аккаунта со страницы товаров
        login_page.open_hamburger_menu()
        page.locator('.bm-item-list a:has-text("Logout")').click()
        # Sauce Demo перенаправляет на login page после logout
        login_page.username_input.wait_for(state="visible", timeout=10000)

        # Войти снова
        login_page.go_to()
        login_page.login()
        inventory_page.wait_for_page_load()

        # Добавить товар и перейти в корзину
        inventory_page.add_product_to_cart(0)
        inventory_page.open_cart()
        cart_page.wait_for_page_load()

        # Открыть меню и выйти
        page.locator(".bm-burger-button").click()
        page.locator('.bm-item-list a:has-text("Logout")').click()
        # Sauce Demo перенаправляет на login page
        login_page.username_input.wait_for(state="visible", timeout=10000)
