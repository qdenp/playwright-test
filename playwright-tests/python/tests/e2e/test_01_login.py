"""
Тест 1: Успешный логин
"""
import pytest
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_successful_login(authenticated_page):
    page = authenticated_page['page']
    inventory_page = authenticated_page['inventory_page']

    assert 'inventory' in page.url
    assert inventory_page.get_title_text() == 'Products'
    assert inventory_page.products.first.is_visible()
