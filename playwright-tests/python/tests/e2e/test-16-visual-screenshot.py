"""
Тест 16: Визуальный скриншот страницы товаров
"""

import re

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_visual_screenshot(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()

    expect(page).to_have_url(re.compile(r".*inventory\.html"))

    # Ждём загрузки страницы товаров
    inventory_page.wait_for_page_load()

    # Скриншот страницы товаров
    page.screenshot(path="test_results/screenshots/products-page.png", full_page=False)
