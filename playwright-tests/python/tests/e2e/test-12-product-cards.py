"""
Product cards on the inventory page
"""

from playwright.sync_api import expect
from constants import EXPECTED_PRODUCT_COUNT
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_product_cards(page):
    """Verify all product details are shown"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Verify each product has a name, description, price and add-to-cart button
    expect(inventory_page.products).to_have_count(EXPECTED_PRODUCT_COUNT)
    expect(inventory_page.product_names).to_have_count(EXPECTED_PRODUCT_COUNT)
    expect(inventory_page.product_descriptions).to_have_count(EXPECTED_PRODUCT_COUNT)
    expect(inventory_page.product_prices).to_have_count(EXPECTED_PRODUCT_COUNT)
    expect(inventory_page.add_to_cart_buttons).to_have_count(EXPECTED_PRODUCT_COUNT)

    # All elements are non-empty and visible
    expect(inventory_page.product_names.first).not_to_be_empty()
    expect(inventory_page.product_descriptions.first).not_to_be_empty()
    expect(inventory_page.product_prices.first).to_contain_text("$")
    expect(inventory_page.add_to_cart_buttons.first).to_be_visible()
