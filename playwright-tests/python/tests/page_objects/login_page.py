"""
Page Object для страницы логина
"""

import os
from playwright.sync_api import Page, expect

from constants import NAVIGATION_TIMEOUT

# Credentials из environment variables (с дефолтными значениями)
DEFAULT_USERNAME = os.getenv("SAUCE_DEMO_USERNAME", "standard_user")
DEFAULT_PASSWORD = os.getenv("SAUCE_DEMO_PASSWORD", "secret_sauce")


class LoginPage:
    def __init__(self, page: Page):
        self.page = page
        self.username_input = page.locator("#user-name")
        self.password_input = page.locator("#password")
        self.login_button = page.locator("#login-button")
        self.error_message = page.locator(".error-message-container")
        self.error_banner = page.get_by_role("alert")
        self.hamburger_menu = page.locator(".bm-burger-button")
        self.close_menu = page.locator(".bm-cross-button")
        self.menu_overlay = page.locator(".bm-cover")

    def go_to(self):
        base_url = os.getenv("SAUCE_DEMO_BASE_URL", "https://www.saucedemo.com")
        self.page.goto(base_url, wait_until="domcontentloaded", timeout=NAVIGATION_TIMEOUT)

    def enter_username(self, username: str):
        self.username_input.clear()
        if username:
            self.username_input.fill(username)

    def enter_password(self, password: str):
        self.password_input.fill(password)

    def click_login(self):
        self.login_button.click()

    def login(self, username: str = None, password: str = None):
        """Логин с credentials из env vars по умолчанию"""
        user = username or DEFAULT_USERNAME
        pwd = password or DEFAULT_PASSWORD
        self.enter_username(user)
        self.enter_password(pwd)
        self.click_login()

    def get_error_message(self) -> str:
        # Пробуем сначала error_banner, потом error_message
        if self.error_banner.is_visible():
            return self.error_banner.inner_text()
        return self.error_message.inner_text()

    def is_error_message_visible(self) -> bool:
        try:
            self.error_message.wait_for(state="visible", timeout=1000)
            return True
        except Exception:
            return False

    def wait_for_error_message(self, timeout: int = 10000):
        """Явное ожидание появления сообщения об ошибке с auto-retry"""
        # Пробуем error_banner (для empty username), потом error_message
        try:
            expect(self.error_banner).to_be_visible(timeout=3000)
        except Exception:
            pass
        expect(self.error_message).to_be_visible(timeout=timeout)

    def open_hamburger_menu(self):
        self.hamburger_menu.click()

    def close_hamburger_menu(self):
        self.close_menu.click()

    def is_hamburger_menu_open(self) -> bool:
        return self.close_menu.is_visible()


