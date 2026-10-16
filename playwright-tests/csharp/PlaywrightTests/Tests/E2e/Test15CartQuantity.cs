using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Cart badge increments by 1 on add and decrements by 1 on remove.</summary>
public sealed class Test15CartQuantity : E2eTestBase
{
    public Test15CartQuantity(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "15-cart-quantity")]
    public async Task Cart_badge_increments_on_add_and_decrements_on_remove()
    {
        await LoginAsStandardUserAsync();

        // The cart starts empty - no badge
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveCountAsync(0);

        // Adding a product increments the badge by 1
        await InventoryPage.AddProductToCartAsync(0);
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("1");

        // Adding a second product increments the badge again
        await InventoryPage.AddProductToCartAsync(1);
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("2");

        // Going to the cart and removing one product decrements the badge
        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(2);

        await CartPage.RemoveItemAsync(0);
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(1);
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("1");

        // Removing the last product clears the badge
        await CartPage.RemoveItemAsync(0);
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(0);
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveCountAsync(0);
    }
}
