"""
Responsive design tests
"""

from playwright.sync_api import expect
from constants import EXPECTED_PRODUCT_COUNT
from page_objects.cart_page import CartPage
from page_objects.checkout_page import CheckoutPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_work_on_mobile_viewport(page):
    """Verify it works on a mobile viewport (375x667)"""
    page.set_viewport_size({"width": 375, "height": 667})

    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    expect(inventory_page.products).to_have_count(EXPECTED_PRODUCT_COUNT)

    login_page.open_hamburger_menu()
    # Verify the menu opened (the close button is visible)
    expect(login_page.close_menu).to_be_visible()
    login_page.close_hamburger_menu()


def test_work_on_tablet_viewport(page):
    """Verify it works on a tablet viewport (768x1024)"""
    page.set_viewport_size({"width": 768, "height": 1024})

    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    expect(inventory_page.products).to_have_count(EXPECTED_PRODUCT_COUNT)


def test_handle_viewport_resize(page):
    """Verify handling a viewport resize mid-interaction"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Switch to mobile
    page.set_viewport_size({"width": 375, "height": 667})
    inventory_page.add_product_to_cart(0)

    # Switch to tablet
    page.set_viewport_size({"width": 768, "height": 1024})
    badge = inventory_page.cart_badge_count
    expect(badge).to_have_text("1")


def test_add_to_cart_on_mobile(page):
    """Verify adding to the cart on a mobile viewport"""
    page.set_viewport_size({"width": 375, "height": 667})

    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.add_product_to_cart(0)
    inventory_page.add_product_to_cart(1)

    badge = inventory_page.cart_badge_count
    expect(badge).to_have_text("2")


def test_checkout_on_tablet(page):
    """Verify checkout on a tablet viewport"""
    page.set_viewport_size({"width": 768, "height": 1024})

    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)
    checkout_page = CheckoutPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.add_product_to_cart(0)
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    cart_page.checkout()
    checkout_page.wait_for_page_load()

    checkout_page.fill_info("Test", "User", "12345")
    checkout_page.click_continue()

    expect(checkout_page.finish_button).to_be_visible(timeout=10000)
