using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Checkout validation with empty fields.</summary>
public sealed class Test21CheckoutValidation : E2eTestBase
{
    public Test21CheckoutValidation(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "21-checkout-validation")]
    public async Task Empty_checkout_fields_report_each_missing_field()
    {
        await LoginAsStandardUserAsync();

        await InventoryPage.AddProductToCartAsync(0);
        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();

        await CartPage.CheckoutAsync();
        await CheckoutPage.WaitForPageLoadAsync();
        await Assertions.Expect(CheckoutPage.FirstNameInput).ToBeVisibleAsync();

        // Click Continue without filling in the fields
        await CheckoutPage.ClickContinueAsync();

        // We stay on the first checkout step
        await Assertions.Expect(CheckoutPage.FirstNameInput).ToBeVisibleAsync();
        await Assertions.Expect(CheckoutPage.ContinueButton).ToBeVisibleAsync();

        // The error names the first missing field
        await Assertions.Expect(CheckoutPage.ErrorMessage).ToContainTextAsync("First Name is required");

        // Fill in the first name only - the error now names the next missing field
        await CheckoutPage.FillFirstNameAsync("Ivan");
        await CheckoutPage.ClickContinueAsync();
        await Assertions.Expect(CheckoutPage.ErrorMessage).ToContainTextAsync("Last Name is required");

        // Fill in the last name only - the error names the last missing field
        await CheckoutPage.FillLastNameAsync("Ivanov");
        await CheckoutPage.ClickContinueAsync();
        await Assertions.Expect(CheckoutPage.ErrorMessage).ToContainTextAsync("Postal Code is required");

        // With all three fields filled the form is accepted
        await CheckoutPage.FillZipCodeAsync("100000");
        await CheckoutPage.ClickContinueAsync();
        await Assertions.Expect(CheckoutPage.FinishButton).ToBeVisibleAsync();
    }
}
