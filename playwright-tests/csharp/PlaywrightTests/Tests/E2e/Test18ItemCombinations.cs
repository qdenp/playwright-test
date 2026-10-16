using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Specific item combinations (3 of 6 products).</summary>
public sealed class Test18ItemCombinations : E2eTestBase
{
    public Test18ItemCombinations(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "18-item-combinations")]
    public async Task The_cheapest_three_products_can_be_added_and_verified()
    {
        await LoginAsStandardUserAsync();

        // Record the catalogue price of every product before picking the cheapest
        var catalogPrices = await InventoryPage.GetAllProductPricesAsync();
        var cheapestThree = catalogPrices.OrderBy(price => price).Take(3).ToList();

        // Add the 3 cheapest: Onesie (7.99), Bike Light (9.99), Red T-Shirt (15.99)
        await InventoryPage.AddProductToCartAsync(4); // Onesie - $7.99
        await InventoryPage.AddProductToCartAsync(1); // Bike Light - $9.99
        await InventoryPage.AddProductToCartAsync(5); // Red T-Shirt - $15.99

        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("3");

        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(3);

        var cartItems = await CartPage.GetCartItemNamesAsync();
        Assert.Contains("Sauce Labs Onesie", cartItems);
        Assert.Contains("Sauce Labs Bike Light", cartItems);
        Assert.Contains("Test.allTheThings() T-Shirt (Red)", cartItems);

        // The three cheapest in the catalogue are the three items in the cart
        var pricesInCart = (await CartPage.GetCartItemPricesAsync()).OrderBy(price => price).ToList();
        Assert.Equal(cheapestThree, pricesInCart);
    }

    [Fact]
    [Trait("Scenario", "18-item-combinations")]
    public async Task The_most_expensive_three_products_can_be_added_and_verified()
    {
        await LoginAsStandardUserAsync();

        var catalogPrices = await InventoryPage.GetAllProductPricesAsync();
        var priciestThree = catalogPrices.OrderByDescending(price => price).Take(3).ToList();

        // Add the 3 most expensive: Fleece Jacket (49.99), Backpack (29.99), Bolt T-Shirt (24.99)
        await InventoryPage.AddProductToCartAsync(3); // Fleece Jacket - $49.99
        await InventoryPage.AddProductToCartAsync(0); // Backpack - $29.99
        await InventoryPage.AddProductToCartAsync(2); // Bolt T-Shirt - $24.99

        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("3");

        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(3);

        var cartItems = await CartPage.GetCartItemNamesAsync();
        Assert.Contains("Sauce Labs Fleece Jacket", cartItems);
        Assert.Contains("Sauce Labs Backpack", cartItems);
        Assert.Contains("Sauce Labs Bolt T-Shirt", cartItems);

        var pricesInCart = (await CartPage.GetCartItemPricesAsync()).OrderByDescending(price => price).ToList();
        Assert.Equal(priciestThree, pricesInCart);
    }

    [Fact]
    [Trait("Scenario", "18-item-combinations")]
    public async Task Alternating_products_can_be_added_and_verified()
    {
        await LoginAsStandardUserAsync();

        // Read the catalogue prices before leaving the inventory page
        var catalogPrices = await InventoryPage.GetAllProductPricesAsync();
        var alternating = new[] { catalogPrices[0], catalogPrices[2], catalogPrices[4] }.OrderBy(price => price).ToList();

        // Add the items at indices 0, 2, 4 (Backpack, Bolt T-Shirt, Onesie)
        await InventoryPage.AddProductToCartAsync(0);
        await InventoryPage.AddProductToCartAsync(2);
        await InventoryPage.AddProductToCartAsync(4);

        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("3");

        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(3);

        var cartItems = await CartPage.GetCartItemNamesAsync();
        Assert.Contains("Sauce Labs Backpack", cartItems);
        Assert.Contains("Sauce Labs Bolt T-Shirt", cartItems);
        Assert.Contains("Sauce Labs Onesie", cartItems);

        // Alternating means the odd-indexed catalogue entries were picked
        var pricesInCart = (await CartPage.GetCartItemPricesAsync()).OrderBy(price => price).ToList();
        Assert.Equal(alternating, pricesInCart);
    }
}
