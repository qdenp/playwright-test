"""
Item detail - viewing and returning
"""

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_item_detail(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Clicking a product name opens the detail panel
    inventory_page.click_product_name(0)

    # The detail panel appears inline - verify the description is visible
    expect(inventory_page.get_detail_description_locator("carry.allTheThings()")).to_be_visible(timeout=5000)

    # Verify the Back to Products button is present
    expect(inventory_page.get_back_to_products_locator()).to_be_visible(timeout=5000)

    # Return to the Products page
    inventory_page.click_back_to_products()
    expect(inventory_page.get_product_name_locator(0)).to_be_visible(timeout=5000)

    # Open the details of the second product
    inventory_page.click_product_name(1)

    # Verify the second product's detail panel
    expect(inventory_page.get_detail_description_locator("carry.allTheThings()")).not_to_be_visible(timeout=5000)
    expect(inventory_page.get_back_to_products_locator()).to_be_visible(timeout=5000)

    # Return to the Products page
    inventory_page.click_back_to_products()
    expect(inventory_page.get_product_name_locator(0)).to_be_visible(timeout=5000)
