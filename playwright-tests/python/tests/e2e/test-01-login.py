"""
Успешный логин
"""

import re

from playwright.sync_api import expect
from constants import EXPECTED_PRODUCT_COUNT


def test_successful_login(authenticated_page):
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]

    expect(page).to_have_url(re.compile(r".*inventory\.html"))
    expect(inventory_page.header_title).to_have_text("Products")
    expect(inventory_page.products).to_have_count(EXPECTED_PRODUCT_COUNT)
