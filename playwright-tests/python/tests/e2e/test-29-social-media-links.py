"""
Social Media Links: the three footer links and how they behave
"""

import re

from playwright.sync_api import expect
from page_objects.inventory_page import InventoryPage
from page_objects.login_page import LoginPage


def test_all_three_social_links_point_at_the_expected_targets(authenticated_page):
    inventory_page = authenticated_page["inventory_page"]

    expect(inventory_page.social_x_link).to_have_attribute(
        "href", "https://x.com/saucelabs"
    )
    expect(inventory_page.social_facebook_link).to_have_attribute(
        "href", "https://www.facebook.com/saucelabs"
    )
    expect(inventory_page.social_linkedin_link).to_have_attribute(
        "href", "https://www.linkedin.com/company/sauce-labs/"
    )


def test_social_links_open_in_a_new_tab(authenticated_page):
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]

    for link in (
        inventory_page.social_x_link,
        inventory_page.social_facebook_link,
        inventory_page.social_linkedin_link,
    ):
        expect(link).to_have_attribute("target", "_blank")
        expect(link).to_have_attribute("rel", re.compile(r"noreferrer"))

    with page.context.expect_page() as new_page_info:
        inventory_page.social_x_link.click()
    new_page = new_page_info.value
    expect(new_page).to_have_url(re.compile(r"x\.com"))
    new_page.close()


def test_shop_stays_usable_after_opening_a_social_link(authenticated_page):
    page = authenticated_page["page"]
    inventory_page = authenticated_page["inventory_page"]

    with page.context.expect_page() as new_page_info:
        inventory_page.social_facebook_link.click()
    new_page = new_page_info.value
    new_page.close()

    expect(page).to_have_url(re.compile(r".*inventory\.html"))
    expect(inventory_page.products).to_have_count(6)
