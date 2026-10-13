using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Sorting products by name.</summary>
public sealed class SortByNameTests : E2eTestBase
{
    public SortByNameTests(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "08-sort-by-name")]
    public async Task Sort_by_name_a_to_z()
    {
        await LoginAsStandardUserAsync();

        await InventoryPage.SortByAlphaAtoZAsync();

        // The list has fully rendered - it holds 6 products
        await Assertions.Expect(InventoryPage.ProductNames).ToHaveCountAsync(TestConfig.ExpectedProductCount);

        var names = await InventoryPage.GetAllProductNamesAsync();

        Assert.Equal(TestConfig.ExpectedProductCount, names.Count);

        for (var i = 1; i < names.Count; i++)
        {
            Assert.True(
                string.Compare(names[i], names[i - 1], StringComparison.OrdinalIgnoreCase) > 0,
                $"\"{names[i]}\" is not after \"{names[i - 1]}\".");
        }
    }
}
