"""
Performance budget tests
"""

import time

import pytest
from playwright.sync_api import expect
from constants import PERFORMANCE_BUDGETS
from page_objects.cart_page import CartPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_load_login_page_within_2_seconds(page, base_url):
    """Verify the login page loads within 2 seconds"""
    start_time = time.time() * 1000
    page.goto(base_url)
    load_time = time.time() * 1000 - start_time

    # Allow up to 3 seconds for slower browsers (Firefox, WebKit)
    assert load_time < PERFORMANCE_BUDGETS["LOGIN_PAGE"]
    print(f"Login page loaded in {load_time}ms")


def test_load_inventory_page_within_3_seconds_after_login(page):
    """Verify the inventory page loads within 3 seconds after login"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    start_time = time.time() * 1000
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()
    load_time = time.time() * 1000 - start_time

    assert load_time < PERFORMANCE_BUDGETS["INVENTORY_PAGE"]
    print(f"Inventory page loaded in {load_time}ms")


def test_load_all_6_products_within_performance_budget(page):
    """Verify all 6 products load within the time budget"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    start_time = time.time() * 1000
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()
    load_time = time.time() * 1000 - start_time

    product_count = inventory_page.get_product_count()
    assert product_count == 6
    assert load_time < PERFORMANCE_BUDGETS["PRODUCTS_LIST"]
    print(f"All 6 products loaded in {load_time}ms")


def test_load_cart_page_within_2_seconds(page, base_url):
    """Verify the cart page loads within 2 seconds"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    start_time = time.time() * 1000
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()
    inventory_page.add_product_to_cart(0)
    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    load_time = time.time() * 1000 - start_time

    assert load_time < PERFORMANCE_BUDGETS["CART_PAGE"]
    print(f"Cart page loaded in {load_time}ms")


def test_verify_page_load_consistency(page):
    """Verify page-load stability (3 runs)"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    load_times = []
    for i in range(3):
        start_time = time.time() * 1000
        login_page.go_to()
        login_page.login()
        inventory_page.wait_for_page_load()
        load_times.append(time.time() * 1000 - start_time)

    avg_load_time = sum(load_times) / len(load_times)
    print(f"Average load time: {avg_load_time}ms")
    assert avg_load_time < PERFORMANCE_BUDGETS["AVERAGE_LOAD"]


def test_verify_product_images_load_within_budget(page):
    """Verify product images load within the time budget"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    start_time = time.time() * 1000
    images = page.locator(".inventory_item_img")
    images.first.wait_for(state="visible")
    load_time = time.time() * 1000 - start_time

    assert load_time < PERFORMANCE_BUDGETS["IMAGES"]
    print(f"Images loaded in {load_time}ms")
