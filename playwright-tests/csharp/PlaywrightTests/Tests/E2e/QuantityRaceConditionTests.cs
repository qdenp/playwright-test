using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Quantity stays at 1 per item, and the add buttons are race-free.</summary>
public sealed class QuantityRaceConditionTests : E2eTestBase
{
    public QuantityRaceConditionTests(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "17-quantity-race-condition")]
    public async Task Every_cart_item_shows_quantity_one_and_offers_no_way_to_edit_it()
    {
        await LoginAsStandardUserAsync();

        await InventoryPage.AddProductToCartAsync(0); // Backpack
        await InventoryPage.AddProductToCartAsync(2); // Bolt T-Shirt
        await InventoryPage.AddProductToCartAsync(4); // Onesie

        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("3");

        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();

        // Every item is displayed with quantity 1
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(3);
        await Assertions.Expect(CartPage.ItemQuantities).ToHaveTextAsync(new[] { "1", "1", "1" });

        // There is no quantity input to edit it with
        await Assertions.Expect(CartPage.QuantityInputs).ToHaveCountAsync(0);
    }

    [Fact]
    [Trait("Scenario", "17-quantity-race-condition")]
    public async Task Every_product_lands_in_the_cart_when_add_buttons_are_clicked_concurrently()
    {
        await LoginAsStandardUserAsync();

        // The catalogue is fully rendered before the buttons are counted
        await Assertions.Expect(InventoryPage.AddToCartButtons)
            .ToHaveCountAsync(TestConfig.ExpectedProductCount);

        var buttonCount = await InventoryPage.AddToCartButtons.CountAsync();

        // Click all add-to-cart buttons without awaiting each one individually
        await Page.EvaluateAsync(
            "count => { const buttons = Array.from(document.querySelectorAll('.btn.btn_primary.btn_small.btn_inventory')); buttons.slice(0, count).forEach(button => button.click()); }",
            buttonCount);

        // All of them must have landed in the cart
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync(buttonCount.ToString());

        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();
        await Assertions.Expect(CartPage.CartItems).ToHaveCountAsync(buttonCount);

        // And none of them can have a quantity other than 1
        await Assertions.Expect(CartPage.ItemQuantities).ToHaveTextAsync(
            Enumerable.Repeat("1", buttonCount).ToArray());
    }
}
