"""
Фикстуры для pytest-playwright
"""

import sys
import os

# Добавляем tests в PYTHONPATH для импортов
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

import pytest


def get_all_credentials():
    """Возвращает словарь всех пользователей и их паролей из SAUCE_DEMO_PASSWORDS"""
    passwords_str = os.getenv("SAUCE_DEMO_PASSWORDS", "")
    if not passwords_str:
        return {}
    credentials = {}
    for pair in passwords_str.split(","):
        username, password = pair.split(":")
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
        ("standard_user", "secret_sauce", True, "Products", None),
        ("problem_user", "secret_sauce", True, "Products", None),
        ("performance_glitch_user", "secret_sauce", True, "Products", None),
        ("error_user", "secret_sauce", True, "Products", None),
        ("visual_user", "secret_sauce", True, "Products", None),
        ("locked_out_user", "secret_sauce", False, None, "locked out"),
        ("standard_user", "wrong_password", False, None, "do not match"),
        ("", "secret_sauce", True, "Products", None),
    ]

    if index is not None and index < len(params):
        return params[index]
    return ("standard_user", "secret_sauce", True, "Products", None)


@pytest.fixture
def authenticated_page(page):
    """Автоматически авторизованная страница"""
    from page_objects.login_page import LoginPage
    from page_objects.inventory_page import InventoryPage

    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()  # Без аргументов — использует credentials из env vars
    inventory_page.wait_for_page_load()

    return {"page": page, "login_page": login_page, "inventory_page": inventory_page}
