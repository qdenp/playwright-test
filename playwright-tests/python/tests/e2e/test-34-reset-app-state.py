"""
Test 34: Reset App State tests
"""

import pytest
from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_reset_cart_via_reset_app_state(page):
    """Проверить что корзина сбрасывается через Reset App State в меню"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Добавить товары в корзину
    inventory_page.add_product_to_cart(0)
    inventory_page.add_product_to_cart(1)
    badge = page.locator(".shopping_cart_badge")
    expect(badge).to_have_text("2")

    # Сбросить состояние приложения через меню
    inventory_page.open_hamburger_menu()
    inventory_page.click_reset_app_state()
    # Перезагрузить страницу для отражения сброса (баг Sauce Demo: UI корзины не обновляется без перезагрузки)
    page.reload()
    inventory_page.wait_for_page_load()

    # Корзина должна быть пустой
    badge_count = inventory_page.get_cart_badge_count()
    assert badge_count == 0


def test_reset_app_state_and_verify_complete_reset(page):
    """Проверить полный сброс состояния приложения"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Добавить товары и перейти в корзину
    inventory_page.add_product_to_cart(0)
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Сбросить состояние приложения через меню
    inventory_page.open_hamburger_menu()
    inventory_page.click_reset_app_state()
    # Перезагрузить страницу для отражения сброса (баг Sauce Demo: UI корзины не обновляется без перезагрузки)
    page.reload()
    # Sauce Demo перенаправляет на страницу логина после сброса
    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Проверить полный сброс
    badge_count = inventory_page.get_cart_badge_count()
    assert badge_count == 0
    assert inventory_page.get_product_count() == 6
