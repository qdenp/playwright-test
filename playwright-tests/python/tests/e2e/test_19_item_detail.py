"""
Тест 19: Item detail — просмотр и возврат
"""
import pytest
from playwright.sync_api import expect
from tests.page_objects.login_page import LoginPage
from tests.page_objects.inventory_page import InventoryPage


def test_item_detail(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Клик по названию первого товара — открывает detail panel
    page.locator('[data-test="inventory-item-name"]').first.click()

    # Detail panel появляется inline — проверяем что description видно
    expect(page.get_by_text('carry.allTheThings()')).to_be_visible(timeout=5000)

    # Проверяем что есть кнопка "Back to Products"
    expect(page.get_by_text('Back to Products')).to_be_visible(timeout=5000)

    # Возвращаемся на Products
    page.get_by_text('Back to Products').click()
    expect(page.locator('[data-test="inventory-item-name"]')).to_be_visible(timeout=5000)

    # Открываем детали второго товара
    inventory_page.products.nth(1).locator('[data-test="inventory-item-name"]').click()

    # Проверяем detail второго товара
    expect(page.get_by_text('carry.allTheThings()')).not_to_be_visible(timeout=5000)
    expect(page.get_by_text('Back to Products')).to_be_visible(timeout=5000)

    # Возвращаемся на Products
    page.get_by_text('Back to Products').click()
    expect(page.locator('[data-test="inventory-item-name"]')).to_be_visible(timeout=5000)
