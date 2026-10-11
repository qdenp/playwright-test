"""
Тест 16: Визуальный скриншот страницы товаров
"""

import pytest
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_visual_screenshot(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()

    assert "inventory" in page.url

    # Ждём загрузки страницы товаров
    inventory_page.wait_for_page_load()

    # Скриншот страницы товаров
    page.screenshot(path="test_results/screenshots/products-page.png", full_page=False)
