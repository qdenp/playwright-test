"""
View a single product and come back to the catalogue
"""

from playwright.sync_api import expect


def test_item_detail_view_and_return(authenticated_page):
    inventory_page = authenticated_page["inventory_page"]

    # Clicking a product name opens the item detail page for that product
    inventory_page.click_product_name(0)
    expect(inventory_page.get_detail_description_locator()).to_be_visible()
    expect(inventory_page.get_back_to_products_locator()).to_be_visible()

    first_description = inventory_page.get_detail_description_locator().inner_text()
    assert first_description

    # Back to products returns to the catalogue
    inventory_page.click_back_to_products()
    expect(inventory_page.get_product_name_locator(0)).to_be_visible()

    # Opening a different product shows its own description
    inventory_page.click_product_name(1)
    expect(inventory_page.get_detail_description_locator()).to_be_visible()

    second_description = inventory_page.get_detail_description_locator().inner_text()
    assert second_description
    assert second_description != first_description

    expect(inventory_page.get_back_to_products_locator()).to_be_visible()
    inventory_page.click_back_to_products()
    expect(inventory_page.get_product_name_locator(0)).to_be_visible()
