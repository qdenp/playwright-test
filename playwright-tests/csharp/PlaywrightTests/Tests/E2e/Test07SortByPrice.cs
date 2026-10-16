using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Sorting products by price.</summary>
public sealed class Test07SortByPrice : E2eTestBase
{
    public Test07SortByPrice(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "07-sort-by-price")]
    public async Task Sort_by_price_low_to_high()
    {
        await LoginAsStandardUserAsync();

        await InventoryPage.SortByPriceLowToHighAsync();
        await Assertions.Expect(InventoryPage.ProductPrices).ToHaveCountAsync(TestConfig.ExpectedProductCount);

        var prices = await InventoryPage.GetAllProductPricesAsync();

        AssertPricesAreSortedAscending(prices);
    }

    [Fact]
    [Trait("Scenario", "07-sort-by-price")]
    public async Task Sort_by_price_high_to_low()
    {
        await LoginAsStandardUserAsync();

        await InventoryPage.SortByPriceHighToLowAsync();
        await Assertions.Expect(InventoryPage.ProductPrices).ToHaveCountAsync(TestConfig.ExpectedProductCount);

        var prices = await InventoryPage.GetAllProductPricesAsync();

        AssertPricesAreSortedDescending(prices);
    }

    private static void AssertPricesAreSortedAscending(List<decimal> prices)
    {
        Assert.Equal(TestConfig.ExpectedProductCount, prices.Count);

        for (var i = 1; i < prices.Count; i++)
        {
            Assert.True(
                prices[i] >= prices[i - 1],
                $"Price {prices[i]:F2} at index {i} is lower than {prices[i - 1]:F2} at index {i - 1}.");
        }

        Assert.True(prices[0] == prices.Min(), $"The cheapest price {prices.Min():F2} is not first ({prices[0]:F2}).");
    }

    private static void AssertPricesAreSortedDescending(List<decimal> prices)
    {
        Assert.Equal(TestConfig.ExpectedProductCount, prices.Count);

        for (var i = 1; i < prices.Count; i++)
        {
            Assert.True(
                prices[i] <= prices[i - 1],
                $"Price {prices[i]:F2} at index {i} is higher than {prices[i - 1]:F2} at index {i - 1}.");
        }

        Assert.True(prices[0] == prices.Max(), $"The most expensive price {prices.Max():F2} is not first ({prices[0]:F2}).");
    }
}
