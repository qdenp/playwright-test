"""
Test 23: Verify product images are displayed
"""

import pytest
from playwright.sync_api import expect
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_display_all_product_images(page):
    """Проверить что все изображения товаров отображаются"""
    login_page = LoginPage(page)
    inventory_page = InventoryPage(page)

    login_page.go_to()
    login_page.login()
    inventory_page.wait_for_page_load()

    # Проверить что все 6 изображений товаров видны
    product_images = page.locator(".inventory_item img")
    expect(product_images).to_have_count(6)

    # Проверить что каждое изображение имеет src атрибут
    images = product_images.all()
    for img in images:
        src = img.get_attribute("src")
        assert src is not None
        assert len(src) > 0
