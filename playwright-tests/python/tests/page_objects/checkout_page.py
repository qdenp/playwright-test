"""
Page Object для оформления заказа (Checkout)
"""

import re

from playwright.sync_api import Download, Page, expect

from constants import DEFAULT_TIMEOUT


class CheckoutPage:
    def __init__(self, page: Page):
        self.page = page
        self.first_name_input = page.locator('[data-test="firstName"]')
        self.last_name_input = page.locator('[data-test="lastName"]')
        self.zip_code_input = page.locator('[data-test="postalCode"]')
        self.zip_input = self.zip_code_input
        self.continue_button = page.get_by_role("button", name="Continue")
        self.finish_button = page.get_by_role("button", name="Finish")
        self.cancel_button = page.locator(".button.btn_secondary.btn_small.btn_footer")
        self.error_message = page.locator(".error-message-container")
        self.complete_title = page.locator(".complete-header")
        self.back_to_products = page.locator(".btn.btn_action.btn_small")
        self.generate_pdf_button = page.get_by_role("button", name="Generate PDF")
        self.items_total_label = page.locator(".summary_subtotal_label")
        self.tax_label = page.locator(".summary_tax_label")
        self.total_label = page.locator(".summary_total_label")
        self.summary_item_prices = page.locator(".cart_item .inventory_item_price")

    def fill_first_name(self, first_name: str):
        self.first_name_input.fill(first_name)

    def fill_last_name(self, last_name: str):
        self.last_name_input.fill(last_name)

    def fill_zip_code(self, zip_code: str):
        self.zip_code_input.fill(zip_code)

    def click_continue(self):
        self.continue_button.click()

    def click_finish(self):
        self.finish_button.click()

    def click_cancel(self):
        self.cancel_button.click()

    def click_back_to_products(self):
        self.back_to_products.click()

    def get_error_message(self) -> str:
        return self.error_message.inner_text()

    def is_error_message_visible(self) -> bool:
        return self.error_message.is_visible()

    def wait_for_page_load(self):
        self.first_name_input.wait_for(state="visible", timeout=DEFAULT_TIMEOUT)

    def submit_checkout_form(self, first_name: str, last_name: str, zip_code: str):
        self.fill_first_name(first_name)
        self.fill_last_name(last_name)
        self.fill_zip_code(zip_code)
        self.click_continue()

    def fill_info(self, first_name: str, last_name: str, zip_code: str):
        self.fill_first_name(first_name)
        self.fill_last_name(last_name)
        self.fill_zip_code(zip_code)

    def generate_pdf(self) -> Download:
        """Экспорт заказа в PDF: клик по кнопке и ожидание скачивания"""
        with self.page.expect_download() as download_info:
            self.generate_pdf_button.click()
        return download_info.value

    @staticmethod
    def parse_money(text):
        """Разбор суммы из строки вида 'Item total: $55.97'."""
        if text is None:
            raise AssertionError("Не найдена сумма в сводке чекаута")
        match = re.search(r"\$\s*([\d,]+\.\d{2})", text)
        if not match:
            raise AssertionError(f'Не удалось разобрать сумму из "{text}"')
        return float(match.group(1).replace(",", ""))

    def get_items_total(self) -> float:
        """Сумма всех товаров из сводки чекаута ('Item total: $55.97')."""
        return self.parse_money(self.items_total_label.text_content())

    def get_tax(self) -> float:
        """Налог из сводки чекаута ('Tax: $4.48')."""
        return self.parse_money(self.tax_label.text_content())

    def get_total(self) -> float:
        """Итоговая сумма заказа ('Total: $60.45')."""
        return self.parse_money(self.total_label.text_content())

    def get_summary_item_prices(self) -> list:
        """Цены отдельных товаров из сводки чекаута, по порядку."""
        return [self.parse_money(t) for t in self.summary_item_prices.all_text_contents()]
