"""
Reset App State tests
"""

from playwright.sync_api import expect
from constants import EXPECTED_PRODUCT_COUNT
from page_objects.cart_page import CartPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_reset_cart_via_reset_app_state(page):
    """Verify the cart resets via Reset App State in the menu"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Add products to the cart
    inventory_page.add_product_to_cart(0)
    inventory_page.add_product_to_cart(1)
    expect(inventory_page.cart_badge_count).to_have_text("2")

    # Reset the app state via the menu
    inventory_page.open_hamburger_menu()
    inventory_page.click_reset_app_state()
    # Reload the page to reflect the reset (SauceDemo bug: the cart UI does not update without a reload)
    page.reload()
    inventory_page.wait_for_page_load()

    # The cart must be empty
    expect(inventory_page.cart_badge_count).to_have_count(0)


def test_reset_app_state_and_verify_complete_reset(page):
    """Verify a full app-state reset"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Add products and go to the cart
    inventory_page.add_product_to_cart(0)
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Reset the app state via the menu
    inventory_page.open_hamburger_menu()
    inventory_page.click_reset_app_state()
    # Reload the page to reflect the reset (SauceDemo bug: the cart UI does not update without a reload)
    page.reload()
    # SauceDemo redirects to the login page after the reset
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Verify the full reset
    expect(inventory_page.cart_badge_count).to_have_count(0)
    expect(inventory_page.products).to_have_count(EXPECTED_PRODUCT_COUNT)
