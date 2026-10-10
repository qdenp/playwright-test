"""
Тест 20: Разные пользователи видят одинаковый каталог
"""
import pytest
from page_objects.login_page import LoginPage, get_all_credentials
from page_objects.inventory_page import InventoryPage


def get_test_users():
    """Возвращает пользователей из env vars или fallback на стандартных"""
    creds = get_all_credentials()
    if creds:
        return list(creds.keys())[:5]
    # Fallback на стандартных пользователей
    return ['standard_user', 'problem_user', 'performance_glitch_user', 'error_user', 'visual_user']


@pytest.mark.parametrize("username", get_test_users())
def test_same_catalog_for_all_users(page, username):
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    # Используем стандартный пароль для fallback пользователей
    password = get_all_credentials().get(username, 'secret_sauce')
    login_page.login(username, password)
    inventory_page.wait_for_page_load()

    # Все пользователи видят 6 товаров
    assert inventory_page.get_product_count() == 6

    # Все видят одинаковые названия товаров
    names = inventory_page.get_all_product_names()
    assert len(names) == 6
    assert 'Sauce Labs Backpack' in names
    assert 'Sauce Labs Bike Light' in names
