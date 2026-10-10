"""
Тест 10: Кросс-браузерная проверка — авторизация
"""

import pytest
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_cross_browser(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()

    assert "inventory" in page.url
    inventory_page.wait_for_page_load()
    assert inventory_page.get_title_text() == "Products"
    assert inventory_page.is_products_visible()
    assert inventory_page.get_product_count() > 0
