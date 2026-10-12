"""
Page Object for the login page
"""

import os
from playwright.sync_api import Page, expect

from constants import DEFAULT_TIMEOUT, ERROR_TIMEOUT, NAVIGATION_TIMEOUT

# Credentials from environment variables (with defaults)
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
        self.close_button = page.locator(".bm-cross-button")
        self.menu_overlay = page.locator(".bm-menu-wrap")

    def go_to(self):
        base_url = os.getenv("SAUCE_DEMO_BASE_URL", "https://www.saucedemo.com")
        self.page.goto(base_url, wait_until="domcontentloaded", timeout=NAVIGATION_TIMEOUT)

    def enter_username(self, username: str):
        self.username_input.clear()
        self.username_input.fill(username)

    def enter_password(self, password: str):
        self.password_input.fill(password)

    def click_login(self):
        self.login_button.click()

    def login(self, username: str = None, password: str = None):
        """Login with the env vars credentials by default.

        Use `is not None` rather than `or`: an empty string is valid input for the
        "empty username" case, whereas `or` would substitute the default.
        """
        user = username if username is not None else DEFAULT_USERNAME
        pwd = password if password is not None else DEFAULT_PASSWORD
        self.enter_username(user)
        self.enter_password(pwd)
        self.click_login()

    def get_error_message(self) -> str:
        # Try error_banner first, then error_message
        if self.error_banner.is_visible():
            return self.error_banner.inner_text()
        return self.error_message.inner_text()

    def is_error_message_visible(self) -> bool:
        try:
            self.error_message.wait_for(state="visible", timeout=ERROR_TIMEOUT)
            return True
        except Exception:
            return False

    def wait_for_error_message(self, timeout: int = DEFAULT_TIMEOUT):
        """Explicitly wait for the error message with auto-retry"""
        expect(self.error_message).to_be_visible(timeout=timeout)

    def open_hamburger_menu(self):
        self.hamburger_menu.click()

    def close_hamburger_menu(self):
        self.close_button.click()

    def is_hamburger_menu_open(self) -> bool:
        return self.close_button.is_visible()

    def get_menu_items(self) -> list:
        """Return the labels of the hamburger menu entries"""
        items = self.page.locator(".bm-item-list a").all_text_contents()
        return [item.strip() for item in items]


