"""
Тест 16: Визуальный скриншот страницы товаров
"""
import pytest
from tests.page_objects.login_page import LoginPage


def test_visual_screenshot(page):
    login_page = LoginPage(page)

    login_page.go_to()
    login_page.login()

    assert 'inventory' in page.url

    # Скриншот страницы товаров
    page.locator('.title').wait_for(state='visible', timeout=10000)
    page.screenshot(path='test_results/screenshots/products-page.png', full_page=False)
