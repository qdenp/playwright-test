"""
Sorting by price (low to high and high to low)
"""

import pytest
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_sort_by_price_low_to_high(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.sort_by_price_low_to_high()

    prices = inventory_page.get_all_product_prices()

    for i in range(1, len(prices)):
        assert prices[i] >= prices[i - 1]
    assert prices[0] == min(prices)


def test_sort_by_price_high_to_low(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.sort_by_price_high_to_low()

    prices = inventory_page.get_all_product_prices()

    for i in range(1, len(prices)):
        assert prices[i] <= prices[i - 1]
    assert prices[0] == max(prices)
