"""
Cart badge increments on add and decrements on remove
"""

from playwright.sync_api import expect
from page_objects.inventory_page import InventoryPage
from page_objects.cart_page import CartPage


def test_cart_badge_increments_and_decrements(authenticated_page):
    inventory_page = authenticated_page["inventory_page"]
    page = authenticated_page["page"]
    cart_page = CartPage(page)

    # The cart starts empty - no badge
    expect(inventory_page.cart_badge_count).to_have_count(0)

    # Adding a product increments the badge by 1
    inventory_page.add_product_to_cart(0)
    expect(inventory_page.cart_badge_count).to_have_text("1")

    # Adding a second product increments the badge again
    inventory_page.add_product_to_cart(1)
    expect(inventory_page.cart_badge_count).to_have_text("2")

    # Going to the cart and removing one product decrements the badge
    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(2)

    cart_page.remove_item(0)
    expect(cart_page.cart_items).to_have_count(1)
    expect(inventory_page.cart_badge_count).to_have_text("1")

    # Removing the last product clears the badge
    cart_page.remove_item(0)
    expect(cart_page.cart_items).to_have_count(0)
    expect(inventory_page.cart_badge_count).to_have_count(0)
