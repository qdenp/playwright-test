"""
Verify product images are displayed
"""

import re

from playwright.sync_api import expect
from constants import EXPECTED_PRODUCT_COUNT
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_display_all_product_images(page):
    """Verify all product images are shown"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Verify all 6 product images are visible
    expect(inventory_page.product_images).to_have_count(EXPECTED_PRODUCT_COUNT)
    expect(inventory_page.product_images.first).to_be_visible()

    # Verify each image has a non-empty src attribute
    for i in range(EXPECTED_PRODUCT_COUNT):
        expect(inventory_page.product_images.nth(i)).to_have_attribute("src", re.compile(r"\S"))
