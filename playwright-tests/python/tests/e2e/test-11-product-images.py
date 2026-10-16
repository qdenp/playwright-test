"""
Verify that all product images are shown
"""

import re

from playwright.sync_api import expect
from constants import EXPECTED_PRODUCT_COUNT


def test_display_all_product_images(authenticated_page):
    inventory_page = authenticated_page["inventory_page"]

    # There are exactly 6 product images
    expect(inventory_page.product_images).to_have_count(EXPECTED_PRODUCT_COUNT)

    # Every one of them is visible
    for i in range(EXPECTED_PRODUCT_COUNT):
        expect(inventory_page.product_images.nth(i)).to_be_visible()

    # Each image has a non-empty src attribute
    for i in range(EXPECTED_PRODUCT_COUNT):
        expect(inventory_page.product_images.nth(i)).to_have_attribute(
            "src", re.compile(r".+")
        )
