using System.Text.RegularExpressions;
using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>All product images are displayed on the inventory page.</summary>
public sealed class ProductImagesTests : E2eTestBase
{
    public ProductImagesTests(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "11-product-images")]
    public async Task All_product_images_are_displayed()
    {
        await LoginAsStandardUserAsync();

        // There are exactly 6 product images and every one of them is visible
        await Assertions.Expect(InventoryPage.ProductImages).ToHaveCountAsync(TestConfig.ExpectedProductCount);

        for (var i = 0; i < TestConfig.ExpectedProductCount; i++)
        {
            await Assertions.Expect(InventoryPage.ProductImages.Nth(i)).ToBeVisibleAsync();
        }

        // Each image has a non-empty src attribute
        for (var i = 0; i < TestConfig.ExpectedProductCount; i++)
        {
            await Assertions.Expect(InventoryPage.ProductImages.Nth(i))
                .ToHaveAttributeAsync("src", new Regex(".+"));
        }
    }
}
