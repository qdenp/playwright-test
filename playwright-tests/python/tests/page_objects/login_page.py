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

    def go_to(self, url: str = ""):
        # Use empty string or relative path to avoid baseURL truncation on GitHub Pages
        # page.goto('/') → https://qdenp.github.io/ (wrong!)
        # page.goto('') → uses baseURL correctly
        path = url.lstrip("/") if url else ""
        self.page.goto(path, wait_until="domcontentloaded", timeout=NAVIGATION_TIMEOUT)

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
