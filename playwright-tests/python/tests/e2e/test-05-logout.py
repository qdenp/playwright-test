"""
Logout - signing out of the account
"""

import re

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_logout(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Ensure that we are on the inventory page
    expect(page).to_have_url(re.compile(r".*inventory\.html"))

    # Open the menu and click Logout
    login_page.open_hamburger_menu()
    inventory_page.click_logout()

    # Verify we are back on the login page (SPA - the URL does not change)
    expect(login_page.username_input).to_be_visible(timeout=10000)
    expect(login_page.password_input).to_be_visible(timeout=10000)
    expect(login_page.login_button).to_be_visible(timeout=10000)
