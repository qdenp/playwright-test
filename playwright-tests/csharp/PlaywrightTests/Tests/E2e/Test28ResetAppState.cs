using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Reset App State clears the cart but not the catalogue.</summary>
public sealed class Test28ResetAppState : E2eTestBase
{
    public Test28ResetAppState(ITestOutputHelper output) : base(output)
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

    [Fact(Skip = "SAUCE_DEMO_BUG: Reset App State requires a page reload to fully work on GitHub Pages, which redirects to login.")]
    [Trait("Scenario", "28-reset-app-state")]
    public void Skipping_Reset_app_state_clears_the_stored_cart_and_not_only_the_badge()
    {
        // SAUCE_DEMO_BUG: Reset App State requires a page reload to fully work.
        // Once the site bug is fixed: remove Skip, uncomment the body.
    }
}
