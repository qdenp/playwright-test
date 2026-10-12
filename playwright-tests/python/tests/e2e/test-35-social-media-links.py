"""
Test 35: Social Media Links
Проверка загрузки страницы после логина
"""

import pytest
from playwright.sync_api import expect
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_verify_page_loads_correctly_after_login(page):
    """Проверить что страница загружается корректно после логина"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    # Логин
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Проверить что страница товаров загрузилась корректно
    expect(inventory_page.products.first).to_be_visible()
    assert inventory_page.get_product_count() == 6


def test_verify_page_structure_and_elements(page):
    """Проверить структуру страницы и элементы"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    # Логин
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Проверить основные элементы страницы
    expect(page.locator(".title")).to_contain_text("Products")
    expect(inventory_page.sort_dropdown).to_be_visible()
    expect(inventory_page.cart_badge).to_be_visible()


def test_verify_all_6_products_are_loaded(page):
    """Проверить что все 6 товаров загружены"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    # Логин
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Проверить что все 6 товаров присутствуют
    product_count = inventory_page.get_product_count()
    assert product_count == 6

    # Проверить что все товары видны
    products = inventory_page.products
    assert products.count() == 6
