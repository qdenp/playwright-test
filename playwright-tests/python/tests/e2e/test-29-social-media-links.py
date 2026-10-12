"""
Social Media Links
Verifying that the page loads after login
"""

from playwright.sync_api import expect
from constants import EXPECTED_PRODUCT_COUNT
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_verify_page_loads_correctly_after_login(page):
    """Verify the page loads correctly after login"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    # Login
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Verify the inventory page loaded correctly
    expect(inventory_page.products).to_have_count(EXPECTED_PRODUCT_COUNT)


def test_verify_page_structure_and_elements(page):
    """Verify the page structure and elements"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    # Login
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Verify the main page elements
    expect(inventory_page.header_title).to_contain_text("Products")
    expect(inventory_page.sort_dropdown).to_be_visible()
    expect(inventory_page.cart_badge).to_be_visible()


def test_verify_all_6_products_are_loaded(page):
    """Verify all 6 products are loaded"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    # Login
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Verify all products are present and visible
    expect(inventory_page.products).to_have_count(EXPECTED_PRODUCT_COUNT)
    expect(inventory_page.products.first).to_be_visible()
