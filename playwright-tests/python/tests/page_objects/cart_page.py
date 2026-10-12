"""
Page Object for the cart page
"""

import re

from playwright.sync_api import Page

from constants import NAVIGATION_TIMEOUT


class CartPage:
    def __init__(self, page: Page):
        self.page = page
        self.cart_items = page.locator(".cart_item")
        self.checkout_button = page.locator(".checkout_button")
        self.continue_shopping_button = page.locator('[data-test="continue-shopping"]')
        self.quantity_inputs = self.cart_items.locator("input")
        self.item_quantities = page.locator('[data-test="item-quantity"]')

    def wait_for_page_load(self):
        """
        Wait for the cart page to load.

        Wait for the URL instead of the items: on an empty cart `.cart_item`
        is absent, so waiting on `.first()` would hang until timeout.
        """
        self.page.wait_for_url(re.compile(r".*cart\.html"), timeout=NAVIGATION_TIMEOUT)

    def get_cart_item_names(self) -> list:
        names = []
        count = self.cart_items.count()
        for i in range(count):
            name = self.cart_items.nth(i).locator('[data-test="inventory-item-name"]').inner_text()
            names.append(name)
        return names

    def get_cart_item_prices(self) -> list:
        """Prices of the individual items in the cart, in order."""
        texts = self.cart_items.locator('[data-test="inventory-item-price"]').all_text_contents()
        return [self.parse_money(t) for t in texts]

    @staticmethod
    def parse_money(text):
        """Parse an amount from a string such as '$29.99'."""
        if text is None:
            raise ValueError("No amount found for the cart item")
        match = re.search(r"\$\s*([\d,]+\.\d{2})", text)
        if not match:
            raise ValueError(f'Could not parse the amount from "{text}"')
        return float(match.group(1).replace(",", ""))

    def remove_item(self, index: int):
        cart_item = self.cart_items.nth(index)
        remove_button = cart_item.locator('button:has-text("Remove")')
        remove_button.click()

    def checkout(self):
        self.checkout_button.click()

    def continue_shopping(self):
        self.continue_shopping_button.click()

    def get_cart_item_count(self) -> int:
        return self.cart_items.count()

    def clear_cart(self):
        count = self.cart_items.count()
        for i in range(count - 1, -1, -1):
            self.remove_item(i)
