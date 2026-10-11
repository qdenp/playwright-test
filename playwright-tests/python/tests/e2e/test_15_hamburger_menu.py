"""
Тест 15: Hamburger меню — открытие и закрытие
"""

import pytest
from page_objects.login_page import LoginPage


def test_hamburger_menu(page):
    login_page = LoginPage(page)

    login_page.go_to()
    login_page.login()

    # Открываем меню
    login_page.open_hamburger_menu()
    assert login_page.close_menu.is_visible()

    # Проверяем количество элементов меню (5 штук)
    menu_items = page.locator(".bm-item-list a")
    assert menu_items.count() == 5

    # Проверяем наличие элементов меню
    assert page.locator('[data-test="inventory-sidebar-link"]').is_visible()
    assert page.locator('[data-test="about-sidebar-link"]').is_visible()
    assert page.locator('[data-test="logout-sidebar-link"]').is_visible()

    # Закрываем меню
    login_page.close_hamburger_menu()
    # Меню закрыто — проверяем, что burger button снова виден
    assert login_page.hamburger_menu.is_visible()
