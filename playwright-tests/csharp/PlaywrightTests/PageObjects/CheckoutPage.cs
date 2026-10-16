using Microsoft.Playwright;
using PlaywrightTests.Config;
using PlaywrightTests.Support;

namespace PlaywrightTests.PageObjects;

/// <summary>
/// Page Object for the two checkout steps and the order-complete page.
/// </summary>
public sealed class CheckoutPage
{
    private readonly IPage _page;

    public CheckoutPage(IPage page)
    {
        _page = page;
    }

    public ILocator FirstNameInput => _page.Locator("[data-test=\"firstName\"]");

    public ILocator LastNameInput => _page.Locator("[data-test=\"lastName\"]");

    public ILocator ZipCodeInput => _page.Locator("[data-test=\"postalCode\"]");

    public ILocator ContinueButton => _page.GetByRole(AriaRole.Button, new() { Name = "Continue" });

    public ILocator FinishButton => _page.GetByRole(AriaRole.Button, new() { Name = "Finish" });

    public ILocator GeneratePdfButton => _page.GetByRole(AriaRole.Button, new() { Name = "Generate PDF" });

    public ILocator ErrorMessage => _page.Locator(".error-message-container");

    public ILocator CompleteHeader => _page.Locator(".complete-header");

    public ILocator ItemsTotalLabel => _page.Locator(".summary_subtotal_label");

    public ILocator TaxLabel => _page.Locator(".summary_tax_label");

    public ILocator TotalLabel => _page.Locator(".summary_total_label");

    public ILocator SummaryItemPrices => _page.Locator(".cart_item .inventory_item_price");

    public async Task WaitForPageLoadAsync()
    {
        await FirstNameInput.WaitForAsync(
            new LocatorWaitForOptions { State = WaitForSelectorState.Visible, Timeout = Timeouts.Navigation });
    }

    public async Task FillFirstNameAsync(string firstName) => await FirstNameInput.FillAsync(firstName);

    public async Task FillLastNameAsync(string lastName) => await LastNameInput.FillAsync(lastName);

    public async Task FillZipCodeAsync(string zipCode) => await ZipCodeInput.FillAsync(zipCode);

    public async Task FillInfoAsync(string firstName, string lastName, string zipCode)
    {
        await FillFirstNameAsync(firstName);
        await FillLastNameAsync(lastName);
        await FillZipCodeAsync(zipCode);
    }

    public async Task ClickContinueAsync() => await ContinueButton.ClickAsync();

    public async Task ClickFinishAsync() => await FinishButton.ClickAsync();

    /// <summary>Export the order as PDF: click the button and wait for the download.</summary>
    public async Task<IDownload> GeneratePdfAsync()
    {
        return await _page.RunAndWaitForDownloadAsync(async () => await GeneratePdfButton.ClickAsync());
    }

    /// <summary>Sum of all items in the checkout summary ("Item total: $55.97").</summary>
    public async Task<decimal> GetItemsTotalAsync() => Money.Parse(await ItemsTotalLabel.TextContentAsync());

    /// <summary>Tax from the checkout summary ("Tax: $4.48").</summary>
    public async Task<decimal> GetTaxAsync() => Money.Parse(await TaxLabel.TextContentAsync());

    /// <summary>Total order amount ("Total: $60.45").</summary>
    public async Task<decimal> GetTotalAsync() => Money.Parse(await TotalLabel.TextContentAsync());

    /// <summary>Prices of the individual items in the checkout summary, in order.</summary>
    public async Task<List<decimal>> GetSummaryItemPricesAsync()
    {
        var texts = await SummaryItemPrices.AllTextContentsAsync();
        return Money.ParseAll(texts);
    }
}
