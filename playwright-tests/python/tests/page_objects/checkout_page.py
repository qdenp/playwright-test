"""
Page Object для оформления заказа (Checkout)
"""

from playwright.sync_api import Page, expect

from constants import DEFAULT_TIMEOUT


class CheckoutPage:
    def __init__(self, page: Page):
        self.page = page
        self.first_name_input = page.locator("#first-name")
        self.last_name_input = page.locator("#last-name")
        self.zip_code_input = page.locator("#postal-code")
        self.continue_button = page.locator("#continue")
        self.error_message = page.locator(".error-message-container.error")
        self.order_summary = page.locator(".summary_subtotal_label")
        self.finish_button = page.locator("#finish")
        self.thank_you_message = page.locator("text=Thank you for your order!")
        self.complete_text = page.locator(".complete-text")
        self.back_to_home_button = page.locator(".btn_action")
        self.items_in_order = page.locator(".cart_item")
        self.item_prices = page.locator(".inventory_item_price")

    def wait_for_page_load(self):
        self.first_name_input.wait_for(state="visible", timeout=DEFAULT_TIMEOUT)

    def fill_info(self, first_name: str, last_name: str, zip_code: str):
        self.first_name_input.fill(first_name)
        self.last_name_input.fill(last_name)
        self.zip_code_input.fill(zip_code)

    def click_continue(self):
        self.continue_button.click()

    def click_finish(self):
        self.finish_button.click()

    def get_error_message(self) -> str:
        return self.error_message.text_content()

    def is_error_message_visible(self) -> bool:
        return self.error_message.is_visible()

    def get_thank_you_message(self) -> str:
        return self.thank_you_message.text_content()

    def is_complete_header_visible(self) -> bool:
        self.thank_you_message.wait_for(state="attached", timeout=10000)
        return self.thank_you_message.count() > 0

    def get_complete_text(self) -> str:
        return self.complete_text.text_content()

    def click_back_to_home(self):
        self.back_to_home_button.click()

    def get_order_summary(self) -> dict:
        summary = {}
        summary["items"] = self.items_in_order.all_text_contents()
        summary["prices"] = self.item_prices.all_text_contents()
        return summary

    def is_continue_button_disabled(self) -> bool:
        return self.continue_button.is_disabled()

    def get_first_name_value(self) -> str:
        return self.first_name_input.input_value()

    def get_last_name_value(self) -> str:
        return self.last_name_input.input_value()

    def get_zip_code_value(self) -> str:
        return self.zip_code_input.input_value()
