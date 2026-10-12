"""
Тест 15: Hamburger меню — открытие и закрытие
"""

import pytest
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_hamburger_menu(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()

    # Открываем меню
    login_page.open_hamburger_menu()
    assert login_page.close_menu.is_visible()

    # Проверяем количество элементов меню (5 штук)
    assert inventory_page.get_menu_item_count() == 5

    # Проверяем наличие элементов меню
    assert inventory_page.is_inventory_link_visible()
    assert inventory_page.is_about_link_visible()
    assert inventory_page.is_logout_link_visible()

    # Закрываем меню
    login_page.close_hamburger_menu()
    # Меню закрыто — проверяем, что burger button снова виден
    assert login_page.hamburger_menu.is_visible()
