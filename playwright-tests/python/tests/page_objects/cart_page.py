"""
Page Object для корзины
"""

from playwright.sync_api import Page, expect


class CartPage:
    def __init__(self, page: Page):
        self.page = page
        self.cart_items = page.locator(".cart_item")
        self.remove_buttons = page.locator('button:has-text("Remove")')
        self.checkout_button = page.locator(".checkout_button")
        self.total_price = page.locator(".summary_subtotal_label")
        self.continue_shopping_button = page.locator('[data-test="continue-shopping"]')

    def wait_for_page_load(self):
        self.cart_items.first.wait_for(state="visible", timeout=10000)

    def get_cart_item_names(self) -> list:
        names = []
        count = self.cart_items.count()
        for i in range(count):
            name = (
                self.cart_items.nth(i)
                .locator('[data-test="inventory-item-name"]')
                .text_content()
            )
            names.append(name)
        return names

    def get_cart_item_quantity(self, index: int) -> int:
        item = self.cart_items.nth(index)
        qty_text = item.locator('[data-test="quantity"]').text_content()
        # Parse "Qty (1)" -> 1
        import re

        match = re.search(r"(\d+)", qty_text)
        return int(match.group(1)) if match else 0

    def change_item_quantity(self, index: int, quantity: int):
        item = self.cart_items.nth(index)
        item.locator('[data-test="quantity"]').fill(str(quantity))

    def get_remove_button_count(self) -> int:
        return self.remove_buttons.count()

    def remove_item(self, index: int):
        cart_item = self.cart_items.nth(index)
        remove_button = cart_item.locator('button:has-text("Remove")')
        remove_button.click()
        # Не ждём detached — просто даём время на обновление
        self.page.wait_for_timeout(500)

    def checkout(self):
        self.checkout_button.click()

    def get_total_price(self) -> str:
        text = self.total_price.text_content()
        return text if text is not None else "$0.00"

    def get_cart_item_count(self) -> int:
        return self.cart_items.count()

    def clear_cart(self):
        count = self.cart_items.count()
        for i in range(count - 1, -1, -1):
            try:
                self.remove_item(i)
            except Exception:
                break
