using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Add all 6 products and verify the cart.</summary>
public sealed class AddAllSixProductsTests : E2eTestBase
{
    public AddAllSixProductsTests(ITestOutputHelper output) : base(output)
    {
    }

    private static readonly string[] ExpectedNames =
    {
        "Sauce Labs Backpack",
        "Sauce Labs Bike Light",
        "Sauce Labs Bolt T-Shirt",
        "Sauce Labs Fleece Jacket",
        "Sauce Labs Onesie",
        "Test.allTheThings() T-Shirt (Red)",
    };

    [Fact]
    [Trait("Scenario", "19-add-all-six-products")]
    public async Task All_six_products_can_be_added_to_the_cart()
    {
        await LoginAsStandardUserAsync();

        // Add all 6 products (indices 0-5)
        for (var i = 0; i < TestConfig.ExpectedProductCount; i++)
        {
            await InventoryPage.AddProductToCartAsync(i);
        }

        // The cart badge shows 6
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync(TestConfig.ExpectedProductCount.ToString());

        // Open the cart - all 6 items are in it
        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(TestConfig.ExpectedProductCount);
    }

    [Fact]
    [Trait("Scenario", "19-add-all-six-products")]
    public async Task Product_count_and_prices_are_valid()
    {
        await LoginAsStandardUserAsync();

        // 6 products loaded
        await Assertions.Expect(InventoryPage.Products).ToHaveCountAsync(TestConfig.ExpectedProductCount);

        // All prices are valid numbers within the catalogue range
        var prices = await InventoryPage.GetAllProductPricesAsync();

        Assert.Equal(TestConfig.ExpectedProductCount, prices.Count);

        foreach (var price in prices)
        {
            Assert.True(price > 0m, $"Catalogue price {price:F2} is not positive.");
            Assert.True(price <= 100m, $"Catalogue price {price:F2} is above the budget of $100.00.");
        }
    }

    [Fact]
    [Trait("Scenario", "19-add-all-six-products")]
    public async Task All_six_products_appear_in_the_cart_with_their_names()
    {
        await LoginAsStandardUserAsync();

        // Add all 6 products
        for (var i = 0; i < TestConfig.ExpectedProductCount; i++)
        {
            await InventoryPage.AddProductToCartAsync(i);
        }

        // Open the cart
        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();

        // Wait for the products to render before reading the list
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(TestConfig.ExpectedProductCount);

        var cartItems = await CartPage.GetCartItemNamesAsync();

        Assert.Equal(TestConfig.ExpectedProductCount, cartItems.Count);

        foreach (var name in ExpectedNames)
        {
            Assert.Contains(name, cartItems);
        }
    }
}
