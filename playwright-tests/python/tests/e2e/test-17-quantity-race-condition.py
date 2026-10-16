"""
Verify the quantity of each item stays 1 and cannot be edited
"""

from playwright.sync_api import expect
from page_objects.cart_page import CartPage


def test_each_item_shows_quantity_1_and_cannot_be_edited(authenticated_page):
    inventory_page = authenticated_page["inventory_page"]
    page = authenticated_page["page"]
    cart_page = CartPage(page)

    inventory_page.add_product_to_cart(0)
    inventory_page.add_product_to_cart(2)
    inventory_page.add_product_to_cart(4)

    expect(inventory_page.cart_badge_count).to_have_text("3")

    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Every item is displayed with quantity 1
    expect(cart_page.cart_items).to_have_count(3)
    expect(cart_page.item_quantities).to_have_text(["1", "1", "1"])

    # There is no quantity input to edit it with
    expect(cart_page.quantity_inputs).to_have_count(0)


def test_adding_all_products_concurrently_keeps_every_quantity_at_1(authenticated_page):
    inventory_page = authenticated_page["inventory_page"]
    page = authenticated_page["page"]
    cart_page = CartPage(page)

    # Click every add-to-cart button in one evaluate call
    count = page.evaluate("""() => {
            const buttons = Array.from(
                document.querySelectorAll('.btn.btn_primary.btn_small.btn_inventory')
            );
            buttons.forEach(b => b.click());
            return buttons.length;
        }""")

    expect(inventory_page.cart_badge_count).to_have_text(str(count))

    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(count)
    expect(cart_page.item_quantities).to_have_text(["1"] * count)
