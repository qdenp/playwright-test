using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Adding a product to the cart.</summary>
public sealed class AddToCartTests : E2eTestBase
{
    public AddToCartTests(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "13-add-to-cart")]
    public async Task Adding_a_product_to_the_cart()
    {
        await LoginAsStandardUserAsync();

        // Add the first product - the badge shows 1
        await InventoryPage.AddProductToCartAsync(0);
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("1");

        // Open the cart and verify the first product
        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(1);

        var firstCartNames = await CartPage.GetCartItemNamesAsync();
        Assert.Single(firstCartNames);
        Assert.NotEmpty(firstCartNames[0]);

        // Return to the inventory page
        await CartPage.ContinueShoppingAsync();
        await InventoryPage.WaitForPageLoadAsync();

        // Add a second product - the badge shows 2
        await InventoryPage.AddProductToCartAsync(1);
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("2");

        // Open the cart and verify both products
        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(2);

        var bothCartNames = await CartPage.GetCartItemNamesAsync();
        Assert.Equal(2, bothCartNames.Count);
        Assert.NotEmpty(bothCartNames[1]);

        // The products must be different
        Assert.NotEqual(bothCartNames[0], bothCartNames[1]);
    }
}
