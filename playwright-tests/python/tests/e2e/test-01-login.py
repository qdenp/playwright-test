"""
Тест 1: Успешный логин
"""

import re

import pytest
from playwright.sync_api import expect
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_successful_login(authenticated_page):
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]

    expect(page).to_have_url(re.compile(r".*inventory\.html"))
    assert inventory_page.get_title_text() == "Products"
    expect(inventory_page.products.first).to_be_visible()
