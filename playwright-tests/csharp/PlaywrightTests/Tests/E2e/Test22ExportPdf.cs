using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Exporting an order as PDF.</summary>
public sealed class Test22ExportPdf : E2eTestBase
{
    public Test22ExportPdf(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "22-export-pdf")]
    public async Task An_order_can_be_exported_as_a_pdf()
    {
        await LoginAsStandardUserAsync();

        // Add products to the cart
        await InventoryPage.AddProductToCartAsync(0);
        await InventoryPage.AddProductToCartAsync(1);
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("2");

        // Proceed to checkout
        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(2);

        await CartPage.CheckoutAsync();
        await CheckoutPage.WaitForPageLoadAsync();

        await CheckoutPage.FillInfoAsync("Ivan", "Ivanov", "100000");
        await CheckoutPage.ClickContinueAsync();

        // Wait for the move to the second checkout step
        await Assertions.Expect(CheckoutPage.FinishButton).ToBeVisibleAsync(VisibleWithin(Timeouts.Navigation));

        // Click Finish
        await CheckoutPage.ClickFinishAsync();

        // Wait for the confirmation page
        await Assertions.Expect(CheckoutPage.CompleteHeader).ToBeVisibleAsync(VisibleWithin(Timeouts.Navigation));

        // Click Generate PDF and wait for the download
        var download = await CheckoutPage.GeneratePdfAsync();

        var pdfPath = await download.PathAsync();
        Assert.False(string.IsNullOrEmpty(pdfPath), "The PDF download produced no file path.");

        // The file exists and it is not empty
        var pdfFile = new FileInfo(pdfPath!);
        Assert.True(pdfFile.Exists, $"The downloaded PDF {pdfPath} does not exist.");
        Assert.True(pdfFile.Length > 0, $"The downloaded PDF {pdfPath} is empty.");
    }
}
