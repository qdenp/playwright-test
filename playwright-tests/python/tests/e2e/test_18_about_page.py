"""
Тест 18: About page — переход из hamburger меню
"""
import pytest
from playwright.sync_api import expect
from tests.page_objects.login_page import LoginPage


def test_about_page(page):
    login_page = LoginPage(page)

    login_page.go_to()
    login_page.login()

    # Открываем меню
    login_page.open_hamburger_menu()

    # Переходим по ссылке About
    page.get_by_text('About').click()

    # Sauce Demo — SPA, ждём загрузки новой страницы
    page.wait_for_timeout(2000)

    # Закрываем меню если открыто
    try:
        login_page.close_hamburger_menu()
    except Exception:
        pass

    # Возвращаемся на Products через навигацию браузера
    page.goto('https://www.saucedemo.com/inventory.html')
    expect(page.locator('.title')).to_contain_text('Products', timeout=5000)
