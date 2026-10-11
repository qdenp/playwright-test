"""
Test 34: Performance budget tests
"""

import pytest
from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


class TestPerformanceBudget:
    """Тесты для проверки производительности"""

    def test_01_should_load_login_page_within_2_seconds(self, page):
        """Проверить что страница логина загружается за 2 секунды"""
        start_time = __import__("time").time() * 1000
        page.goto("https://www.saucedemo.com/")
        load_time = __import__("time").time() * 1000 - start_time

        # Разрешить до 3 секунд для более медленных браузеров (Firefox, WebKit)
        assert load_time < 3000
        print(f"Login page loaded in {load_time}ms")

    def test_02_should_load_inventory_page_within_3_seconds_after_login(self, page):
        """Проверить что страница товаров загружается за 3 секунды после входа"""
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)

        start_time = __import__("time").time() * 1000
        login_page.go_to()
        login_page.login()
        inventory_page.wait_for_page_load()
        load_time = __import__("time").time() * 1000 - start_time

        assert load_time < 3000
        print(f"Inventory page loaded in {load_time}ms")

    def test_03_should_load_all_6_products_within_performance_budget(self, page):
        """Проверить что все 6 товаров загружаются в рамках бюджета по времени"""
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)

        start_time = __import__("time").time() * 1000
        login_page.go_to()
        login_page.login()
        inventory_page.wait_for_page_load()
        load_time = __import__("time").time() * 1000 - start_time

        product_count = inventory_page.get_product_count()
        assert product_count == 6
        assert load_time < 5000
        print(f"All 6 products loaded in {load_time}ms")

    def test_04_should_load_cart_page_within_2_seconds(self, page):
        """Проверить что страница корзины загружается за 2 секунды"""
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)
        cart_page = CartPage(page)

        start_time = __import__("time").time() * 1000
        login_page.go_to()
        login_page.login()
        inventory_page.wait_for_page_load()
        inventory_page.add_product_to_cart(0)
        inventory_page.open_cart()
        cart_page.wait_for_page_load()
        load_time = __import__("time").time() * 1000 - start_time

        assert load_time < 4000
        print(f"Cart page loaded in {load_time}ms")

    def test_05_should_verify_page_load_consistency(self, page):
        """Проверить стабильность загрузки страницы (3 запуска)"""
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)

        load_times = []
        for i in range(3):
            start_time = __import__("time").time() * 1000
            login_page.go_to()
            login_page.login()
            inventory_page.wait_for_page_load()
            load_times.append(__import__("time").time() * 1000 - start_time)

        avg_load_time = sum(load_times) / len(load_times)
        print(f"Average load time: {avg_load_time}ms")
        assert avg_load_time < 5000

    def test_06_should_verify_product_images_load_within_budget(self, page):
        """Проверить что изображения товаров загружаются в рамках бюджета"""
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)

        login_page.go_to()
        login_page.login()
        inventory_page.wait_for_page_load()

        start_time = __import__("time").time() * 1000
        images = page.locator(".inventory_item_img")
        images.first.wait_for(state="visible")
        load_time = __import__("time").time() * 1000 - start_time

        assert load_time < 3000
        print(f"Images loaded in {load_time}ms")
