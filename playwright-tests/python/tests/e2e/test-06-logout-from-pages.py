"""
Verify signing out from the inventory page and from the cart page
"""

import re

from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_logout_from_the_inventory_page(authenticated_page):
    page = authenticated_page["page"]
    login_page = authenticated_page["login_page"]
    inventory_page = authenticated_page["inventory_page"]

    inventory_page.open_hamburger_menu()
    inventory_page.click_logout()

    # Logout really happened: the login form is back
    expect(login_page.username_input).to_be_visible()
    expect(login_page.login_button).to_be_visible()

    # The inventory page is not reachable without logging in again
    page.goto("inventory.html", wait_until="domcontentloaded")
    expect(login_page.username_input).to_be_visible()


def test_logout_from_the_cart_page(authenticated_page):
    page = authenticated_page["page"]
    login_page = authenticated_page["login_page"]
    inventory_page = authenticated_page["inventory_page"]
    cart_page = CartPage(page)

    inventory_page.add_product_to_cart(0)
    expect(inventory_page.cart_badge_count).to_have_text("1")

    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    inventory_page.open_hamburger_menu()
    inventory_page.click_logout()

    expect(login_page.username_input).to_be_visible()
    expect(login_page.login_button).to_be_visible()

    # Logging back in returns to the inventory page
    login_page.login()
    inventory_page.wait_for_page_load()
    expect(page).to_have_url(re.compile(r".*inventory\.html"))
