"""
Checkout form validation: what happens with missing fields
"""

from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.checkout_page import CheckoutPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_checkout_validation_empty_fields(page):
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
    expect(checkout_page.first_name_input).to_be_visible()

    # Click Continue without filling in the fields
    checkout_page.click_continue()

    # We stay on the first checkout step
    expect(checkout_page.first_name_input).to_be_visible()
    expect(checkout_page.continue_button).to_be_visible()

    # The error names the first missing field
    expect(checkout_page.error_message).to_contain_text("First Name is required")

    # Fill in the first name only - the error now names the next missing field
    checkout_page.fill_first_name("Ivan")
    checkout_page.click_continue()
    expect(checkout_page.error_message).to_contain_text("Last Name is required")

    # Fill in the last name only - the error names the last missing field
    checkout_page.fill_last_name("Ivanov")
    checkout_page.click_continue()
    expect(checkout_page.error_message).to_contain_text("Postal Code is required")

    # With all three fields filled the form is accepted
    checkout_page.fill_zip_code("100000")
    checkout_page.click_continue()
    expect(checkout_page.finish_button).to_be_visible()
