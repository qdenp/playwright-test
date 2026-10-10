"""
Page Object для страницы логина
"""
import os
from playwright.sync_api import Page, expect


# Credentials из environment variables (с дефолтными значениями)
DEFAULT_USERNAME = os.getenv('SAUCE_DEMO_USERNAME', 'standard_user')
DEFAULT_PASSWORD = os.getenv('SAUCE_DEMO_PASSWORD', 'secret_sauce')


def get_all_credentials():
    """Возвращает словарь всех пользователей и их паролей из SAUCE_DEMO_PASSWORDS"""
    passwords_str = os.getenv('SAUCE_DEMO_PASSWORDS', '')
    if not passwords_str:
        return {}
    credentials = {}
    for pair in passwords_str.split(','):
        username, password = pair.split(':')
        credentials[username] = password
    return credentials


def get_login_params(index=None):
    """
    Возвращает параметры для параметризованного теста.
    Если index=None — возвращает стандартные credentials.
    Если index — возвращает credentials из списка пользователей.
    """
    all_creds = get_all_credentials()
    valid_users = list(all_creds.keys())

    params = [
        ('standard_user', 'secret_sauce', True, 'Products', None),
        ('problem_user', 'secret_sauce', True, 'Products', None),
        ('performance_glitch_user', 'secret_sauce', True, 'Products', None),
        ('error_user', 'secret_sauce', True, 'Products', None),
        ('visual_user', 'secret_sauce', True, 'Products', None),
        ('locked_out_user', 'secret_sauce', False, None, 'locked out'),
        ('standard_user', 'wrong_password', False, None, 'do not match'),
        ('', 'secret_sauce', True, 'Products', None),
    ]

    if index is not None and index < len(params):
        return params[index]
    return (DEFAULT_USERNAME, DEFAULT_PASSWORD, True, 'Products', None)


class LoginPage:
    def __init__(self, page: Page):
        self.page = page
        self.username_input = page.locator('#user-name')
        self.password_input = page.locator('#password')
        self.login_button = page.locator('#login-button')
        self.error_message = page.locator('.error-message-container')
        self.error_banner = page.get_by_role('alert')
        self.hamburger_menu = page.locator('.bm-burger-button')
        self.close_menu = page.locator('.bm-cross-button')

    def go_to(self):
        base_url = os.getenv('SAUCE_DEMO_BASE_URL', 'https://www.saucedemo.com')
        self.page.goto(base_url, wait_until='domcontentloaded', timeout=60000)

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
        return self.error_message.count() > 0

    def wait_for_error_message(self, timeout: int = 10000):
        """Явное ожидание появления сообщения об ошибке с auto-retry"""
        # Пробуем error_banner (для empty username), потом error_message
        try:
            expect(self.error_banner).to_be_visible(timeout=3000)
        except:
            pass
        expect(self.error_message).to_be_visible(timeout=timeout)
        # Небольшая пауза для появления текста
        self.page.wait_for_timeout(300)

    def open_hamburger_menu(self):
        self.hamburger_menu.click()

    def close_hamburger_menu(self):
        self.close_menu.click()

    def is_hamburger_menu_open(self) -> bool:
        return self.close_menu.is_visible()

    def click_back_to_products(self):
        self.page.get_by_text('Back to Products').click()

    def click_logout(self):
        self.page.get_by_role('menuitem', name='Logout').click()
