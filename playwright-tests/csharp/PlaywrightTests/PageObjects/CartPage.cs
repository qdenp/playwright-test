using System.Text.RegularExpressions;
using Microsoft.Playwright;
using PlaywrightTests.Config;
using PlaywrightTests.Support;

namespace PlaywrightTests.PageObjects;

/// <summary>
/// Page Object for the cart page.
/// </summary>
public sealed class CartPage
{
    private readonly IPage _page;

    public CartPage(IPage page)
    {
        _page = page;
    }

    public ILocator CartItems => _page.Locator(".cart_item");

    public ILocator CheckoutButton => _page.Locator(".checkout_button");

    public ILocator ContinueShoppingButton => _page.Locator("[data-test=\"continue-shopping\"]");

    /// <summary>Quantity inputs of the cart items - SauceDemo has none.</summary>
    public ILocator QuantityInputs => CartItems.Locator("input");

    public ILocator ItemQuantities => _page.Locator("[data-test=\"item-quantity\"]");

    /// <summary>
    /// Wait for the cart page to load.
    /// GitHub Pages: use load state instead of URL to avoid baseURL issues.
    /// </summary>
    public async Task WaitForPageLoadAsync()
    {
        await _page.WaitForLoadStateAsync(Microsoft.Playwright.LoadState.NetworkIdle, new() { Timeout = Timeouts.Navigation });
    }

    public async Task<List<decimal>> GetCartItemPricesAsync()
    {
        var texts = await CartItems.Locator("[data-test=\"inventory-item-price\"]").AllTextContentsAsync();
        return Money.ParseAll(texts);
    }

    public async Task<List<string>> GetCartItemNamesAsync()
    {
        var names = await CartItems.Locator("[data-test=\"inventory-item-name\"]").AllTextContentsAsync();
        return names.Select(name => name.Trim()).Where(name => name.Length > 0).ToList();
    }

    public async Task RemoveItemAsync(int index)
    {
        await CartItems.Nth(index).Locator("button", new() { HasText = "Remove" }).ClickAsync();
    }

    public async Task CheckoutAsync() => await CheckoutButton.ClickAsync();

    public async Task ContinueShoppingAsync() => await ContinueShoppingButton.ClickAsync();

    public async Task<int> GetCartItemCountAsync() => await CartItems.CountAsync();
}
