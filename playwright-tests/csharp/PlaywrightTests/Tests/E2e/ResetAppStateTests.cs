using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Reset App State clears the cart but not the catalogue.</summary>
public sealed class ResetAppStateTests : E2eTestBase
{
    public ResetAppStateTests(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "28-reset-app-state")]
    public async Task Reset_app_state_clears_the_cart_badge()
    {
        await LoginAsStandardUserAsync();

        for (var i = 0; i < 3; i++)
        {
            await InventoryPage.AddProductToCartAsync(i);
        }

        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("3");

        await InventoryPage.OpenHamburgerMenuAsync();
        await InventoryPage.ResetAppStateAsync();
        await InventoryPage.CloseHamburgerMenuAsync();

        // The badge disappears immediately - the cart is empty
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveCountAsync(0);
    }

    [Fact]
    [Trait("Scenario", "28-reset-app-state")]
    public async Task Reset_app_state_clears_the_stored_cart_and_not_only_the_badge()
    {
        await LoginAsStandardUserAsync();

        for (var i = 0; i < 3; i++)
        {
            await InventoryPage.AddProductToCartAsync(i);
        }

        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(3);

        await InventoryPage.OpenHamburgerMenuAsync();
        await InventoryPage.ResetAppStateAsync();
        await InventoryPage.CloseHamburgerMenuAsync();

        // Re-open the cart so the page re-reads the stored state
        await Page.GotoAsync("/cart.html", new PageGotoOptions { WaitUntil = WaitUntilState.DOMContentLoaded });
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(0);

        // The catalogue itself is untouched
        await Page.GotoAsync("/inventory.html", new PageGotoOptions { WaitUntil = WaitUntilState.DOMContentLoaded });
        await Assertions.Expect(InventoryPage.Products).ToHaveCountAsync(TestConfig.ExpectedProductCount);
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveCountAsync(0);
    }
}
