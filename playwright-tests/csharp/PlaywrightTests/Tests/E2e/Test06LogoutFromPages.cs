using System.Text.RegularExpressions;
using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Logout from the inventory page and from the cart page.</summary>
public sealed class Test06LogoutFromPages : E2eTestBase
{
    public Test06LogoutFromPages(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "06-logout-from-pages")]
    public async Task Logout_from_the_inventory_page()
    {
        await LoginAsStandardUserAsync();

        await InventoryPage.OpenHamburgerMenuAsync();
        await InventoryPage.ClickLogoutAsync();

        // Logout really happened: the login form is back and the inventory is gone
        await Assertions.Expect(LoginPage.UsernameInput).ToBeVisibleAsync(VisibleWithin(Timeouts.Default));
        await Assertions.Expect(LoginPage.LoginButton).ToBeVisibleAsync();
        await Assertions.Expect(InventoryPage.HeaderTitle).ToBeHiddenAsync();

        // The inventory page is no longer reachable without logging in again
        await Page.GotoAsync("inventory.html", new PageGotoOptions { WaitUntil = WaitUntilState.DOMContentLoaded });
        await Assertions.Expect(LoginPage.UsernameInput).ToBeVisibleAsync();
    }

    [Fact]
    [Trait("Scenario", "06-logout-from-pages")]
    public async Task Logout_from_the_cart_page()
    {
        await LoginAsStandardUserAsync();

        await InventoryPage.AddProductToCartAsync(0);
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("1");

        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();

        await InventoryPage.OpenHamburgerMenuAsync();
        await InventoryPage.ClickLogoutAsync();

        await Assertions.Expect(LoginPage.UsernameInput).ToBeVisibleAsync(VisibleWithin(Timeouts.Default));
        await Assertions.Expect(LoginPage.LoginButton).ToBeVisibleAsync();

        // Logging back in returns to the inventory page
        await LoginPage.LoginAsync();
        await InventoryPage.WaitForPageLoadAsync();
        await Assertions.Expect(Page).ToHaveURLAsync(new Regex("inventory\\.html"));
    }
}
