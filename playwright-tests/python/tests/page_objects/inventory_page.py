"""
Page Object для страницы товаров (Inventory)
"""

import os
from playwright.sync_api import Page, expect


class InventoryPage:
    def __init__(self, page: Page):
        self.page = page
        self.products = page.locator(".inventory_item")
        self.sort_dropdown = page.locator(".product_sort_container")
        self.cart_badge = page.locator(".shopping_cart_link")
        self.header_title = page.locator(".title")

    def go_to(self):
        base_url = os.getenv("SAUCE_DEMO_BASE_URL", "https://www.saucedemo.com")
        self.page.goto(f"{base_url}/inventory.html", wait_until="domcontentloaded", timeout=15000)

    def wait_for_page_load(self):
        self.header_title.wait_for(state="visible", timeout=10000)

    def get_product_count(self) -> int:
        return self.products.count()

    def get_product_element(self, index: int):
        return self.products.nth(index)

    def add_product_to_cart(self, index: int):
        product = self.products.nth(index)
        product.locator(".btn.btn_primary.btn_small.btn_inventory").click()

    def add_product_by_name(self, product_name: str):
        product = self.page.locator(f'.inventory_item:has-text("{product_name}")')
        product.locator(".btn.btn_primary.btn_small.btn_inventory").click()

    def get_product_price(self, index: int) -> float:
        product = self.products.nth(index)
        price_text = product.locator(".inventory_item_price").text_content()
        if price_text is None:
            return 0.0
        return float(price_text.replace("$", ""))

    def get_product_description(self, index: int) -> str:
        product = self.products.nth(index)
        return product.locator(".inventory_item_desc").text_content()

    def get_product_title(self, index: int) -> str:
        product = self.products.nth(index)
        return product.locator(".inventory_item_name").text_content()

    def get_cart_badge_count(self) -> int:
        badge = self.cart_badge.locator(".shopping_cart_badge")
        if not badge.is_visible():
            return 0
        text = badge.text_content()
        return int(text) if text else 0

    def open_cart(self):
        self.cart_badge.click()

    def sort_by_price_low_to_high(self):
        self.sort_dropdown.select_option("Price (low to high)")

    def sort_by_price_high_to_low(self):
        self.sort_dropdown.select_option("Price (high to low)")

    def sort_by_name_a_to_z(self):
        self.sort_dropdown.select_option("Name (A to Z)")

    def sort_by_name_z_to_a(self):
        self.sort_dropdown.select_option("Name (Z to A)")

    def is_products_visible(self) -> bool:
        return self.products.first.is_visible()

    def get_title_text(self) -> str:
        return self.header_title.text_content()

    def open_hamburger_menu(self):
        self.page.locator(".bm-burger-button").click()

    def click_reset_app_state(self):
        self.page.get_by_text("Reset App State").click()

    def get_all_product_names(self) -> list:
        names = []
        count = self.get_product_count()
        for i in range(count):
            names.append(self.get_product_title(i))
        return names

    def click_about(self):
        self.page.locator('[data-test="about-sidebar-link"]').click()

    def get_all_product_prices(self) -> list:
        prices = []
        count = self.get_product_count()
        for i in range(count):
            prices.append(self.get_product_price(i))
        return prices

    # Методы для работы с detail panel товаров
    def click_product_name(self, index: int):
        """Клик по названию товара - открывает detail panel"""
        self.products.nth(index).locator('[data-test="inventory-item-name"]').click()

    def click_back_to_products(self):
        """Клик Back to Products - возвращает на Products страницу"""
        self.page.get_by_text("Back to Products").click()

    def get_detail_description_locator(self, text: str):
        """Возвращает locator для описания товара в detail panel"""
        return self.page.get_by_text(text)

    def get_back_to_products_locator(self):
        """Возвращает locator для кнопки Back to Products"""
        return self.page.get_by_text("Back to Products")

    def get_product_name_locator(self, index: int):
        """Возвращает locator для названия товара"""
        return self.products.nth(index).locator('[data-test="inventory-item-name"]')
