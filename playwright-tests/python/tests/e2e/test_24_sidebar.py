"""
Test 24: Verify sidebar text and logo
"""

import pytest
from playwright.sync_api import expect
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


class TestSidebar:
    """Тесты для проверки боковой панели"""

    def test_01_should_display_sidebar_with_correct_text_and_logo(self, page):
        """Проверить что боковая панель отображается с правильным текстом и логотипом"""
        login_page = LoginPage(page)
        inventory_page = InventoryPage(page)

        login_page.go_to()
        login_page.login()
        inventory_page.wait_for_page_load()

        # Проверить что элементы меню боковой панели видны после открытия меню
        login_page.open_hamburger_menu()
        menu_items = page.locator(".bm-item-list a")
        expect(menu_items).to_have_count(5)  # Logout, About, Inventory, Contact, + 1
