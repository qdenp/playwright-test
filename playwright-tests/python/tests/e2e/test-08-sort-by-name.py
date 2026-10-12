"""
Сортировка по имени (a→z)
"""

import pytest
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage


def test_sort_by_name(page):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.sort_by_name_a_to_z()

    names = inventory_page.get_all_product_names()

    for i in range(1, len(names)):
        assert names[i].lower() > names[i - 1].lower()
