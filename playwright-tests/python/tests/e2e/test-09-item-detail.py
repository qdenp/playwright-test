"""
Item detail — просмотр и возврат
"""

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_item_detail(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Клик по названию первого товара — открывает detail panel
    inventory_page.click_product_name(0)

    # Detail panel появляется inline — проверяем что description видно
    expect(inventory_page.get_detail_description_locator("carry.allTheThings()")).to_be_visible(timeout=5000)

    # Проверяем что есть кнопка "Back to Products"
    expect(inventory_page.get_back_to_products_locator()).to_be_visible(timeout=5000)

    # Возвращаемся на Products
    inventory_page.click_back_to_products()
    expect(inventory_page.get_product_name_locator(0)).to_be_visible(timeout=5000)

    # Открываем детали второго товара
    inventory_page.click_product_name(1)

    # Проверяем detail второго товара
    expect(inventory_page.get_detail_description_locator("carry.allTheThings()")).not_to_be_visible(timeout=5000)
    expect(inventory_page.get_back_to_products_locator()).to_be_visible(timeout=5000)

    # Возвращаемся на Products
    inventory_page.click_back_to_products()
    expect(inventory_page.get_product_name_locator(0)).to_be_visible(timeout=5000)
