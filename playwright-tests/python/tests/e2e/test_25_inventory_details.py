"""
Test 25: Verify inventory item details
"""

import pytest
from playwright.sync_api import expect
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


class TestInventoryDetails:
    """Тесты для проверки деталей товаров"""

    def test_01_should_display_all_inventory_item_details(self, page):
        """Проверить что все детали товаров отображаются"""
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)

        login_page.go_to()
        login_page.login()
        inventory_page.wait_for_page_load()

        # Проверить что каждый товар имеет название, описание, цену и кнопку добавления в корзину
        products = inventory_page.products.all()
        assert len(products) == 6

        for i in range(len(products)):
            product = products[i]

            # Проверить что у товара есть название
            name = product.locator(".inventory_item_name").text_content()
            assert name is not None
            assert len(name) > 0

            # Проверить что у товара есть описание
            desc = product.locator(".inventory_item_desc").text_content()
            assert desc is not None

            # Проверить что у товара есть цена
            price = product.locator(".inventory_item_price").text_content()
            assert price is not None
            assert "$" in price

            # Проверить что кнопка добавления в корзину видна
            expect(
                product.locator(".btn.btn_primary.btn_small.btn_inventory")
            ).to_be_visible()
