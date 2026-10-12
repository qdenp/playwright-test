"""
Browser navigation tests
"""

import pytest
from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.checkout_page import CheckoutPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_handle_browser_back_button(page):
    """Проверить кнопку назад из корзины на страницу товаров"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.add_product_to_cart(0)
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Вернуться назад на страницу товаров
    page.go_back()
    inventory_page.wait_for_page_load()

    expect(page).to_have_url("https://www.saucedemo.com/inventory.html")
    expect(inventory_page.header_title).to_contain_text("Products")


def test_handle_browser_forward_button(page):
    """Проверить кнопку вперед после кнопки назад"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.add_product_to_cart(0)
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Назад
    page.go_back()
    inventory_page.wait_for_page_load()

    # Вперед
    page.go_forward()
    cart_page.wait_for_page_load()

    expect(page).to_have_url("https://www.saucedemo.com/cart.html")


def test_handle_back_from_checkout_to_cart(page):
    """Проверить возврат из оформления заказа в корзину"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)
    checkout_page = CheckoutPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.add_product_to_cart(0)
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    cart_page.checkout()
    checkout_page.wait_for_page_load()

    # Вернуться в корзину
    page.go_back()
    cart_page.wait_for_page_load()

    expect(page).to_have_url("https://www.saucedemo.com/cart.html")


def test_preserve_cart_state_after_navigation(page):
    """Проверить что корзина сохраняет состояние после навигации назад/вперед"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    inventory_page.add_product_to_cart(0)
    inventory_page.add_product_to_cart(1)
    badge = inventory_page.cart_badge_count
    expect(badge).to_have_text("2")

    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    # Навигация назад и вперед
    page.go_back()
    inventory_page.wait_for_page_load()
    page.go_forward()
    cart_page.wait_for_page_load()

    # Корзина должна сохранить 2 товара
    expect(badge).to_have_text("2")
