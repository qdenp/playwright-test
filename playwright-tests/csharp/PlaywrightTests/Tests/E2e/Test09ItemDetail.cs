using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Viewing an item and returning to the catalogue.</summary>
public sealed class Test09ItemDetail : E2eTestBase
{
    public Test09ItemDetail(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "09-item-detail")]
    public async Task Item_detail_can_be_viewed_and_returned_from()
    {
        await LoginAsStandardUserAsync();

        // Clicking a product name opens the item detail page for that product
        await InventoryPage.ClickProductNameAsync(0);
        await Assertions.Expect(InventoryPage.DetailDescription).ToBeVisibleAsync();
        await Assertions.Expect(InventoryPage.BackToProductsButton).ToBeVisibleAsync();

        var firstDescription = await InventoryPage.DetailDescription.InnerTextAsync();
        Assert.NotEmpty(firstDescription);

        // Back to products returns to the catalogue
        await InventoryPage.ClickBackToProductsAsync();
        await Assertions.Expect(InventoryPage.GetProductName(0)).ToBeVisibleAsync();

        // Opening a different product shows its own description
        await InventoryPage.ClickProductNameAsync(1);
        await Assertions.Expect(InventoryPage.DetailDescription).ToBeVisibleAsync();

        var secondDescription = await InventoryPage.DetailDescription.InnerTextAsync();
        Assert.NotEmpty(secondDescription);
        Assert.NotEqual(firstDescription, secondDescription);

        await Assertions.Expect(InventoryPage.BackToProductsButton).ToBeVisibleAsync();
        await InventoryPage.ClickBackToProductsAsync();
        await Assertions.Expect(InventoryPage.GetProductName(0)).ToBeVisibleAsync();
    }
}
