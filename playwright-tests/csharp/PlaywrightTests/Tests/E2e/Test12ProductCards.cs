using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Product cards on the inventory page.</summary>
public sealed class Test12ProductCards : E2eTestBase
{
    public Test12ProductCards(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "12-product-cards")]
    public async Task Every_product_card_has_a_name_a_description_a_price_and_an_add_button()
    {
        await LoginAsStandardUserAsync();

        // Each card carries a name, description, price and an add-to-cart button
        await Assertions.Expect(InventoryPage.Products).ToHaveCountAsync(TestConfig.ExpectedProductCount);
        await Assertions.Expect(InventoryPage.ProductNames).ToHaveCountAsync(TestConfig.ExpectedProductCount);
        await Assertions.Expect(InventoryPage.ProductDescriptions).ToHaveCountAsync(TestConfig.ExpectedProductCount);
        await Assertions.Expect(InventoryPage.ProductPrices).ToHaveCountAsync(TestConfig.ExpectedProductCount);
        await Assertions.Expect(InventoryPage.AddToCartButtons).ToHaveCountAsync(TestConfig.ExpectedProductCount);

        // All elements are non-empty and visible
        await Assertions.Expect(InventoryPage.ProductNames.First).Not.ToBeEmptyAsync();
        await Assertions.Expect(InventoryPage.ProductDescriptions.First).Not.ToBeEmptyAsync();
        await Assertions.Expect(InventoryPage.ProductPrices.First).ToContainTextAsync("$");
        await Assertions.Expect(InventoryPage.AddToCartButtons.First).ToBeVisibleAsync();
    }
}
