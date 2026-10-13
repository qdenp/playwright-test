using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Removing a product from the cart.</summary>
public sealed class RemoveFromCartTests : E2eTestBase
{
    public RemoveFromCartTests(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "14-remove-from-cart")]
    public async Task Removing_a_product_from_the_cart()
    {
        await LoginAsStandardUserAsync();

        await InventoryPage.AddProductToCartAsync(0);
        await InventoryPage.AddProductToCartAsync(1);
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("2");

        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(2);

        await CartPage.RemoveItemAsync(0);
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(1);

        await Page.ReloadAsync();
        await CartPage.WaitForPageLoadAsync();
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("1");
    }
}
