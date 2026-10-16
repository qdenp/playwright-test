using System.Text.RegularExpressions;
using Microsoft.Playwright;
using PlaywrightTests.Config;
using PlaywrightTests.Support;

namespace PlaywrightTests.PageObjects;

/// <summary>
/// Page Object for the inventory (catalogue) page and, where the reference does it,
/// the shared header, the hamburger menu and the social links.
/// </summary>
public sealed class InventoryPage
{
    private readonly IPage _page;

    public InventoryPage(IPage page)
    {
        _page = page;
    }

    public ILocator Products => _page.Locator(".inventory_item");

    public ILocator SortDropdown => _page.Locator(".product_sort_container");

    public ILocator CartLink => _page.Locator(".shopping_cart_link");

    /// <summary>Cart badge - absent when the cart is empty.</summary>
    public ILocator CartBadge => _page.Locator(".shopping_cart_badge");

    public ILocator HeaderTitle => _page.Locator(".title");

    public ILocator InventoryLink => _page.Locator("[data-test=\"inventory-sidebar-link\"]");

    public ILocator AboutLink => _page.Locator("[data-test=\"about-sidebar-link\"]");

    public ILocator LogoutLink => _page.Locator("[data-test=\"logout-sidebar-link\"]");

    public ILocator ResetStateLink => _page.Locator("[data-test=\"reset-sidebar-link\"]");

    public ILocator ProductNames => _page.Locator(".inventory_item_name");

    public ILocator ProductDescriptions => _page.Locator(".inventory_item_desc");

    public ILocator ProductPrices => _page.Locator(".inventory_item_price");

    public ILocator ProductImages => _page.Locator(".inventory_item img");

    public ILocator ProductImageContainers => _page.Locator(".inventory_item_img");

    public ILocator AddToCartButtons => _page.Locator(".btn.btn_primary.btn_small.btn_inventory");

    public ILocator SocialXLink => _page.Locator("[data-test=\"social-x\"]");

    public ILocator SocialFacebookLink => _page.Locator("[data-test=\"social-facebook\"]");

    public ILocator SocialLinkedinLink => _page.Locator("[data-test=\"social-linkedin\"]");

    public ILocator HamburgerButton => _page.Locator(".bm-burger-button");

    public ILocator CloseMenuButton => _page.Locator(".bm-cross-button");

    public ILocator MenuItems => _page.Locator(".bm-item-list a");

    public async Task WaitForPageLoadAsync()
    {
        await HeaderTitle.WaitForAsync(
            new LocatorWaitForOptions { State = WaitForSelectorState.Visible, Timeout = Timeouts.Navigation });
    }

    public ILocator GetProduct(int index) => Products.Nth(index);

    public async Task AddProductToCartAsync(int index)
    {
        await Products.Nth(index).Locator(".btn.btn_primary.btn_small.btn_inventory").ClickAsync();
    }

    public async Task<List<decimal>> GetAllProductPricesAsync()
    {
        var texts = await ProductPrices.AllTextContentsAsync();
        return Money.ParseAll(texts);
    }

    public async Task<List<string>> GetAllProductNamesAsync()
    {
        var names = await ProductNames.AllTextContentsAsync();
        return names.Select(name => name.Trim()).ToList();
    }

    public async Task<int> GetProductCountAsync() => await Products.CountAsync();

    public async Task OpenCartAsync()
    {
        await CartLink.ClickAsync();
        await _page.WaitForURLAsync("**cart*", new() { Timeout = Timeouts.Default });
    }

    public Task SortByPriceLowToHighAsync() =>
        SortDropdown.SelectOptionAsync(new SelectOptionValue { Label = "Price (low to high)" });

    public Task SortByPriceHighToLowAsync() =>
        SortDropdown.SelectOptionAsync(new SelectOptionValue { Label = "Price (high to low)" });

    public Task SortByAlphaAtoZAsync() =>
        SortDropdown.SelectOptionAsync(new SelectOptionValue { Label = "Name (A to Z)" });

    public Task SortByAlphaZtoAAsync() =>
        SortDropdown.SelectOptionAsync(new SelectOptionValue { Label = "Name (Z to A)" });

    /// <summary>Item detail page: open the product at <paramref name="index"/>.</summary>
    public async Task ClickProductNameAsync(int index)
    {
        await Products.Nth(index).Locator("[data-test=\"inventory-item-name\"]").ClickAsync();
    }

    public async Task ClickBackToProductsAsync()
    {
        await _page.Locator("[data-test=\"back-to-products\"]").ClickAsync();
    }

    /// <summary>
    /// Description of the product shown on the item detail page.
    /// Scoped by class because "inventory-item-desc" also exists once per product
    /// in the catalogue grid.
    /// </summary>
    public ILocator DetailDescription => _page.Locator(".inventory_details_desc");

    public ILocator BackToProductsButton => _page.Locator("[data-test=\"back-to-products\"]");

    public ILocator GetProductName(int index) => Products.Nth(index).Locator("[data-test=\"inventory-item-name\"]");

    public async Task OpenHamburgerMenuAsync() => await HamburgerButton.ClickAsync();

    public async Task CloseHamburgerMenuAsync() => await CloseMenuButton.ClickAsync();

    /// <summary>Reset the app state (empty the cart) from the hamburger menu.</summary>
    public async Task ResetAppStateAsync() => await ResetStateLink.ClickAsync();

    public async Task ClickLogoutAsync() => await LogoutLink.ClickAsync();

    public async Task WaitForFirstProductImageAsync()
    {
        await ProductImageContainers.First.WaitForAsync(
            new LocatorWaitForOptions { State = WaitForSelectorState.Visible, Timeout = Timeouts.Default });
    }
}
