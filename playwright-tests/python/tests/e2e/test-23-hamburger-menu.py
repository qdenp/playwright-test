"""
Hamburger menu - opening and closing
"""

from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_hamburger_menu(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()

    # Open the menu
    inventory_page.open_hamburger_menu()
    expect(inventory_page.close_button).to_be_visible()

    # Verify the menu has 5 items
    expect(inventory_page.get_menu_items_locator()).to_have_count(5)

    # Verify the menu items are present
    expect(inventory_page.inventory_link).to_be_visible()
    expect(inventory_page.about_link).to_be_visible()
    expect(inventory_page.logout_link).to_be_visible()

    # Close the menu
    inventory_page.close_hamburger_menu()
    expect(inventory_page.close_button).to_be_hidden()
