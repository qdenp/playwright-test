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
    /// Wait for the cart page to load. The URL is waited for instead of the items:
    /// an empty cart has no ".cart_item", so waiting on it would time out.
    /// </summary>
    public async Task WaitForPageLoadAsync()
    {
        await _page.WaitForURLAsync(
            new Regex(".*cart\\.html"),
            new PageWaitForURLOptions { Timeout = Timeouts.Navigation });
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
