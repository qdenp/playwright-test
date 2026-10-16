"""
Logout - signing out of the account
"""

import re

from playwright.sync_api import expect
from constants import DEFAULT_TIMEOUT
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_logout(authenticated_page):
    page = authenticated_page["page"]
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    # Ensure that we are on the inventory page
    expect(page).to_have_url(re.compile(r".*inventory\.html"))

    # Open the menu and click Logout
    inventory_page.open_hamburger_menu()
    inventory_page.click_logout()

    # Logging out brings the login form back
    expect(login_page.username_input).to_be_visible(timeout=DEFAULT_TIMEOUT)
    expect(login_page.password_input).to_be_visible(timeout=DEFAULT_TIMEOUT)
    expect(login_page.login_button).to_be_visible(timeout=DEFAULT_TIMEOUT)

    # The inventory page is not reachable any more
    expect(inventory_page.header_title).to_be_hidden()
