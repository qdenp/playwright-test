"""
Фикстуры для pytest-playwright
"""
import sys
import os

# Добавляем tests в PYTHONPATH для импортов
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

import pytest


@pytest.fixture
def authenticated_page(page):
    """Автоматически авторизованная страница"""
    from page_objects.login_page import LoginPage
    from page_objects.inventory_page import InventoryPage

    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()  # Без аргументов — использует credentials из env vars
    inventory_page.wait_for_page_load()

    return {'page': page, 'login_page': login_page, 'inventory_page': inventory_page}
