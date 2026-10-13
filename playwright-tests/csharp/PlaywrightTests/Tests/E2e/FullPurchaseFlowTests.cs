using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Full purchase flow (E2E).</summary>
public sealed class FullPurchaseFlowTests : E2eTestBase
{
    public FullPurchaseFlowTests(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "20-full-purchase-flow")]
    public async Task A_full_purchase_from_login_to_order_confirmation()
    {
        await LoginAsStandardUserAsync();

        await InventoryPage.AddProductToCartAsync(0);
        await InventoryPage.AddProductToCartAsync(1);
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("2");

        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(2);

        await CartPage.CheckoutAsync();
        await CheckoutPage.WaitForPageLoadAsync();

        await CheckoutPage.FillInfoAsync("Ivan", "Ivanov", "100000");
        await CheckoutPage.ClickContinueAsync();

        // Wait for the move to the second checkout step
        await Assertions.Expect(CheckoutPage.FinishButton).ToBeVisibleAsync(VisibleWithin(Timeouts.Default));

        // Verify the order totals: the item total equals the sum of the item prices
        var itemPrices = await CheckoutPage.GetSummaryItemPricesAsync();

        Assert.Equal(2, itemPrices.Count);

        var itemsSum = itemPrices.Aggregate(decimal.Zero, (sum, price) => sum + price);
        var itemsTotal = await CheckoutPage.GetItemsTotalAsync();
        AssertAmountEqual(itemsSum, itemsTotal);

        // tax = 8% of the item total
        var tax = await CheckoutPage.GetTaxAsync();
        AssertAmountEqual(itemsTotal * TestConfig.CheckoutTaxRate, tax);

        // total = item total + tax
        var total = await CheckoutPage.GetTotalAsync();
        AssertAmountEqual(itemsTotal + tax, total);

        await CheckoutPage.ClickFinishAsync();

        await Assertions.Expect(CheckoutPage.CompleteHeader).ToBeVisibleAsync(VisibleWithin(Timeouts.Default));
        await Assertions.Expect(CheckoutPage.CompleteHeader).ToContainTextAsync("Thank you for your order");
    }
}
