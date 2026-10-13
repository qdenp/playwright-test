using System.Text.RegularExpressions;
using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Browser navigation: back, forward and cart state preservation.</summary>
public sealed class BrowserNavigationTests : E2eTestBase
{
    public BrowserNavigationTests(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "26-browser-navigation")]
    public async Task The_browser_back_button_returns_from_the_cart_to_the_inventory()
    {
        await LoginAsStandardUserAsync();

        await InventoryPage.AddProductToCartAsync(0);
        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();

        // Go back to the inventory
        await Page.GoBackAsync();
        await InventoryPage.WaitForPageLoadAsync();

        await Assertions.Expect(Page).ToHaveURLAsync(new Regex("inventory"));
        await Assertions.Expect(InventoryPage.HeaderTitle).ToContainTextAsync("Products");
    }

    [Fact]
    [Trait("Scenario", "26-browser-navigation")]
    public async Task The_browser_forward_button_goes_back_to_the_cart()
    {
        await LoginAsStandardUserAsync();

        await InventoryPage.AddProductToCartAsync(0);
        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();

        // Go back
        await Page.GoBackAsync();
        await InventoryPage.WaitForPageLoadAsync();

        // Go forward
        await Page.GoForwardAsync();
        await CartPage.WaitForPageLoadAsync();

        await Assertions.Expect(Page).ToHaveURLAsync(new Regex("cart"));
    }

    [Fact]
    [Trait("Scenario", "26-browser-navigation")]
    public async Task Going_back_from_checkout_returns_to_the_cart()
    {
        await LoginAsStandardUserAsync();

        await InventoryPage.AddProductToCartAsync(0);
        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();

        await CartPage.CheckoutAsync();
        await CheckoutPage.WaitForPageLoadAsync();

        // Go back to the cart
        await Page.GoBackAsync();
        await CartPage.WaitForPageLoadAsync();

        await Assertions.Expect(Page).ToHaveURLAsync(new Regex("cart"));
    }

    [Fact]
    [Trait("Scenario", "26-browser-navigation")]
    public async Task The_cart_survives_multiple_back_and_forward_navigations()
    {
        await LoginAsStandardUserAsync();

        await InventoryPage.AddProductToCartAsync(0);
        await InventoryPage.AddProductToCartAsync(1);
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("2");

        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();

        // Navigate back and forth
        await Page.GoBackAsync();
        await InventoryPage.WaitForPageLoadAsync();
        await Page.GoForwardAsync();
        await CartPage.WaitForPageLoadAsync();

        // The cart still has 2 items
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("2");
    }
}
