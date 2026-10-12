"""
Exporting an order as PDF
"""

import pytest
from playwright.sync_api import expect
from constants import NAVIGATION_TIMEOUT
from page_objects.login_page import LoginPage
from page_objects.inventory_page import InventoryPage
from page_objects.cart_page import CartPage
from page_objects.checkout_page import CheckoutPage


def test_export_pdf_order(page, tmp_path):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)
    checkout_page = CheckoutPage(page)

    # Add products to the cart
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.add_product_to_cart(0)
    inventory_page.add_product_to_cart(1)
    expect(inventory_page.cart_badge_count).to_have_text("2")

    # Proceed to checkout
    inventory_page.open_cart()
    cart_page.wait_for_page_load()
    expect(cart_page.cart_items).to_have_count(2)

    cart_page.checkout()
    checkout_page.wait_for_page_load()

    checkout_page.fill_info("Ivan", "Ivanov", "100000")
    checkout_page.click_continue()

    # Wait for the move to checkout-step-two (Firefox/WebKit are slower)
    expect(checkout_page.finish_button).to_be_visible(timeout=NAVIGATION_TIMEOUT)

    # Click Finish
    checkout_page.click_finish()

    # Wait for the confirmation page
    expect(checkout_page.complete_title).to_be_visible(timeout=NAVIGATION_TIMEOUT)

    # Save the PDF into the tests folder
    pdf_path = tmp_path / "order.pdf"

    # Click Generate PDF and wait for the download
    download = checkout_page.generate_pdf()
    download.save_as(pdf_path)

    # Verify that the file was created
    assert pdf_path.exists()
    assert pdf_path.stat().st_size > 0
