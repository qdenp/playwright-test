"""
Performance budget tests

Measurements use warmup + averaging to avoid cold-start noise (AGENTS.md §3.2).

Inventory-page measurements use re-login (not page.reload()) because the
test site is a static SPA on GitHub Pages: /inventory.html triggers a 404
fallback that redirects to / (login). The auth cookie survives, but
page.reload() lands on the login page instead of the inventory page.
"""

import time

import pytest
from playwright.sync_api import expect
from constants import PERFORMANCE_BUDGETS
from page_objects.cart_page import CartPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage

MEASUREMENT_COUNT = 3


def _relogin(login_page, inventory_page):
    """Navigate to login, log in, wait for inventory."""
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()


def test_load_login_page_within_2_seconds(page, base_url):
    """Verify the login page loads within 2 seconds"""
    # Warmup — prime caches
    page.goto(base_url)
    page.wait_for_load_state("networkidle")

    load_times = []
    for _ in range(MEASUREMENT_COUNT):
        start_time = time.time() * 1000
        page.goto(base_url)
        page.wait_for_load_state("networkidle")
        load_times.append(time.time() * 1000 - start_time)

    avg_load_time = sum(load_times) / len(load_times)
    assert avg_load_time < PERFORMANCE_BUDGETS["LOGIN_PAGE"]
    print(f"Login page avg load time: {avg_load_time:.0f}ms over {MEASUREMENT_COUNT} runs")


def test_load_inventory_page_within_3_seconds_after_login(page):
    """Verify the inventory page loads within 3 seconds after login"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    # Warmup — re-login once to prime caches
    _relogin(login_page, inventory_page)

    load_times = []
    for _ in range(MEASUREMENT_COUNT):
        start_time = time.time() * 1000
        _relogin(login_page, inventory_page)
        load_times.append(time.time() * 1000 - start_time)

    avg_load_time = sum(load_times) / len(load_times)
    assert avg_load_time < PERFORMANCE_BUDGETS["INVENTORY_PAGE"]
    print(f"Inventory page avg load time: {avg_load_time:.0f}ms over {MEASUREMENT_COUNT} runs")


def test_load_all_6_products_within_performance_budget(page):
    """Verify all 6 products load within the time budget"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    # Warmup
    _relogin(login_page, inventory_page)

    load_times = []
    for _ in range(MEASUREMENT_COUNT):
        start_time = time.time() * 1000
        _relogin(login_page, inventory_page)
        load_times.append(time.time() * 1000 - start_time)

    avg_load_time = sum(load_times) / len(load_times)
    assert inventory_page.get_product_count() == 6
    assert avg_load_time < PERFORMANCE_BUDGETS["PRODUCTS_LIST"]
    print(f"All 6 products avg load time: {avg_load_time:.0f}ms over {MEASUREMENT_COUNT} runs")


def test_load_cart_page_within_2_seconds(page):
    """Verify the cart page loads within 2 seconds"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    # Login first to reach the inventory page
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Warmup — add product, open cart, then return to inventory
    inventory_page.add_product_to_cart(0)
    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    cart_page.continue_shopping()
    inventory_page.wait_for_page_load()

    load_times = []
    for _ in range(MEASUREMENT_COUNT):
        start_time = time.time() * 1000
        inventory_page.open_cart()
        cart_page.wait_for_page_load()
        load_times.append(time.time() * 1000 - start_time)
        # Navigate back to inventory for the next iteration
        cart_page.continue_shopping()
        inventory_page.wait_for_page_load()

    avg_load_time = sum(load_times) / len(load_times)
    assert avg_load_time < PERFORMANCE_BUDGETS["CART_PAGE"]
    print(f"Cart page avg load time: {avg_load_time:.0f}ms over {MEASUREMENT_COUNT} runs")


def test_verify_page_load_consistency_3_runs(page):
    """Verify page-load stability (3 runs with warmup)"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    # Warmup
    _relogin(login_page, inventory_page)

    load_times = []
    for _ in range(MEASUREMENT_COUNT):
        start_time = time.time() * 1000
        _relogin(login_page, inventory_page)
        load_times.append(time.time() * 1000 - start_time)

    avg_load_time = sum(load_times) / len(load_times)
    print(f"Average load time: {avg_load_time:.0f}ms")
    assert avg_load_time < PERFORMANCE_BUDGETS["AVERAGE_LOAD"]


def test_verify_product_images_load_within_budget(page):
    """Verify product images load within the time budget"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    # Warmup
    _relogin(login_page, inventory_page)
    inventory_page.product_images.first.wait_for(state="visible")

    load_times = []
    for _ in range(MEASUREMENT_COUNT):
        start_time = time.time() * 1000
        _relogin(login_page, inventory_page)
        inventory_page.product_images.first.wait_for(state="visible")
        load_times.append(time.time() * 1000 - start_time)

    avg_load_time = sum(load_times) / len(load_times)
    assert avg_load_time < PERFORMANCE_BUDGETS["IMAGES"]
    print(f"Images avg load time: {avg_load_time:.0f}ms over {MEASUREMENT_COUNT} runs")
