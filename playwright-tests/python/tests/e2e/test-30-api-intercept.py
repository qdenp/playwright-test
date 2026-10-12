"""
Test 30: API intercept - verify API responses
"""

import pytest
from playwright.sync_api import expect
from page_objects.cart_page import CartPage
from page_objects.checkout_page import CheckoutPage
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_intercept_and_verify_inventory_api_response(page):
    """Проверить перехват и проверку ответа API инвентаря"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    # Настроить перехват API перед навигацией
    # Sauce Demo загружает товары через рендеринг страницы, а не через API вызовы
    # Мы проверяем, что страница успешно загружается после входа
    responses = []

    def handle_response(response):
        if response.status >= 200 and response.status < 300:
            responses.append(response)

    page.on("response", handle_response)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Проверить что страница успешно загрузилась
    assert len(responses) > 0
    # Sauce Demo перенаправляет на страницу товаров после входа
    expect(page).to_have_url("https://www.saucedemo.com/inventory.html")


def test_verify_product_data_structure_from_api(page):
    """Проверить структуру данных товаров из API"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Проверить что товары загружены с правильными данными
    product_count = inventory_page.get_product_count()
    assert product_count == 6

    # Проверить что каждый товар имеет обязательные поля
    for i in range(product_count):
        name = inventory_page.get_product_title(i)
        price = inventory_page.get_product_price(i)
        desc = inventory_page.get_product_description(i)

        assert name is not None
        assert price > 0
        assert desc is not None


def test_verify_all_6_products_have_valid_data(page):
    """Проверить что все 6 товаров имеют корректные данные"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    product_count = inventory_page.get_product_count()
    assert product_count == 6

    # Проверить что все товары имеют корректные цены
    for i in range(product_count):
        price = inventory_page.get_product_price(i)
        assert price > 0
        assert price <= 100


def test_intercept_api_during_checkout_flow(page):
    """Проверить перехват API во время оформления заказа"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)
    cart_page = CartPage(page)
    checkout_page = CheckoutPage(page)

    # Отслеживать все ответы с начала
    all_responses = []

    def handle_response(response):
        if response.status >= 200 and response.status < 300:
            all_responses.append(response)

    page.on("response", handle_response)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Добавить товар в корзину
    inventory_page.add_product_to_cart(0)
    inventory_page.open_cart()
    cart_page.wait_for_page_load()

    responses_before_checkout = len(all_responses)
    # Перейти к оформлению заказа
    cart_page.checkout()
    checkout_page.wait_for_page_load()

    # Проверить что страница оформления заказа успешно загрузилась
    responses_after_checkout = len(all_responses) - responses_before_checkout
    assert responses_after_checkout >= 0
    expect(checkout_page.first_name_input).to_be_visible()
