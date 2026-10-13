using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Adding and removing all products.</summary>
public sealed class AddRemoveAllTests : E2eTestBase
{
    public AddRemoveAllTests(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "16-add-remove-all")]
    public async Task Adding_and_removing_all_products()
    {
        await LoginAsStandardUserAsync();

        await Assertions.Expect(InventoryPage.Products).ToHaveCountAsync(TestConfig.ExpectedProductCount);

        // Add all products
        for (var i = 0; i < TestConfig.ExpectedProductCount; i++)
        {
            await InventoryPage.AddProductToCartAsync(i);
        }

        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync(TestConfig.ExpectedProductCount.ToString());

        // Go to the cart
        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(TestConfig.ExpectedProductCount);

        // Remove all products
        for (var i = 0; i < TestConfig.ExpectedProductCount; i++)
        {
            await CartPage.RemoveItemAsync(0);
            await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(TestConfig.ExpectedProductCount - 1 - i);
        }

        // The cart must be empty
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(0);

        // Reload in order to verify the badge
        await Page.ReloadAsync();
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveCountAsync(0);
    }
}
