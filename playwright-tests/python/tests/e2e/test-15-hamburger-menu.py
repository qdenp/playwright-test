"""
Тест 15: Hamburger меню — открытие и закрытие
"""

from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_hamburger_menu(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()

    # Открываем меню
    login_page.open_hamburger_menu()
    expect(login_page.close_menu).to_be_visible()

    # Проверяем количество элементов меню (5 штук)
    expect(inventory_page.get_menu_items_locator()).to_have_count(5)

    # Проверяем наличие элементов меню
    expect(inventory_page.inventory_link).to_be_visible()
    expect(inventory_page.about_link).to_be_visible()
    expect(inventory_page.logout_link).to_be_visible()

    # Закрываем меню
    login_page.close_hamburger_menu()
    expect(login_page.hamburger_menu).to_be_visible()
