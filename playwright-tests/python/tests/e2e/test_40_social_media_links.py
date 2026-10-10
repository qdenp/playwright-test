"""
Test 40: Social Media Links
Проверка социальных ссылок внизу экрана (X, Facebook, LinkedIn)
"""

import pytest
from playwright.sync_api import expect
from page_objects.inventory_page import InventoryPage


class TestSocialMediaLinks:
    """Тесты для социальных ссылок"""

    def test_01_should_verify_all_social_media_links_are_present(self, page):
        """Проверить что все социальные ссылки присутствуют"""
        # Перейти на главную
        page.goto("/")

        # Проверить что ссылки на социальные сети видны в футере
        # X (Twitter)
        x_link = page.locator('a:has-text("X")')
        expect(x_link).to_be_visible()
        expect(x_link).to_have_attribute("href", "https://x.com/")

        # Facebook
        facebook_link = page.locator('a:has-text("Facebook")')
        expect(facebook_link).to_be_visible()
        expect(facebook_link).to_have_attribute(
            "href", "https://www.facebook.com/saucedemo/"
        )

        # LinkedIn
        linkedin_link = page.locator('a:has-text("LinkedIn")')
        expect(linkedin_link).to_be_visible()
        expect(linkedin_link).to_have_attribute(
            "href", "https://www.linkedin.com/company/saucelabs/"
        )

    def test_02_should_verify_social_links_open_in_new_tab(self, page):
        """Проверить что социальные ссылки открываются в новой вкладке"""
        page.goto("/")

        # Получить обещание новой вкладки перед кликом
        with page.expect_popup() as popup_info:
            page.locator('a:has-text("X")').click(target="_blank")

        new_page = popup_info.value

        # Проверить что новая вкладка открылась
        assert new_page is not None

        # Закрыть новую вкладку
        new_page.close()

    def test_03_should_verify_all_social_links_have_correct_urls_after_login(
        self, page
    ):
        """Проверить URL социальных ссылок после логина"""
        inventory_page = InventoryPage(page)

        # Логин
        page.goto("/")
        page.locator("#user-name").fill("standard_user")
        page.locator("#password").fill("secret_sauce")
        page.locator("#login-button").click()
        inventory_page.wait_for_page_load()

        # Проверить что социальные ссылки всё ещё видны на странице каталога
        x_link = page.locator('a:has-text("X")')
        facebook_link = page.locator('a:has-text("Facebook")')
        linkedin_link = page.locator('a:has-text("LinkedIn")')

        expect(x_link).to_be_visible()
        expect(facebook_link).to_be_visible()
        expect(linkedin_link).to_be_visible()

        # Проверить URL
        expect(x_link).to_have_attribute("href", "https://x.com/")
        expect(facebook_link).to_have_attribute(
            "href", "https://www.facebook.com/saucedemo/"
        )
        expect(linkedin_link).to_have_attribute(
            "href", "https://www.linkedin.com/company/saucelabs/"
        )
