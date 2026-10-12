"""
Reset App State clears the cart
"""

from playwright.sync_api import expect
from constants import EXPECTED_PRODUCT_COUNT
from page_objects.cart_page import CartPage


def test_reset_app_state_clears_the_cart_badge(authenticated_page):
    inventory_page = authenticated_page["inventory_page"]

    for i in range(3):
        inventory_page.add_product_to_cart(i)
    expect(inventory_page.cart_badge_count).to_have_text("3")

    inventory_page.open_hamburger_menu()
    inventory_page.reset_app_state()
    inventory_page.close_hamburger_menu()

    # The badge disappears immediately - the cart is empty
    expect(inventory_page.cart_badge_count).to_have_count(0)


def test_reset_app_state_clears_the_stored_cart(authenticated_page):
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]
    cart_page = CartPage(page)

    for i in range(3):
        inventory_page.add_product_to_cart(i)

    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(3)

    inventory_page.open_hamburger_menu()
    inventory_page.reset_app_state()
    inventory_page.close_hamburger_menu()

    # Re-open the cart so the page re-reads the stored state
    page.goto("/cart.html", wait_until="domcontentloaded")
    expect(cart_page.cart_items).to_have_count(0)

    # The catalogue itself is untouched
    page.goto("/inventory.html", wait_until="domcontentloaded")
    expect(inventory_page.products).to_have_count(EXPECTED_PRODUCT_COUNT)
    expect(inventory_page.cart_badge_count).to_have_count(0)
