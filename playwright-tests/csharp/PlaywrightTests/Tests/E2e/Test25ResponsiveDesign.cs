using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Responsive design: mobile, tablet and viewport resizing.</summary>
public sealed class Test25ResponsiveDesign : E2eTestBase
{
    public Test25ResponsiveDesign(ITestOutputHelper output)
        : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "25-responsive-design")]
    public async Task The_shop_works_on_a_mobile_viewport()
    {
        await Page.SetViewportSizeAsync(MobileViewport.Width, MobileViewport.Height);

        await LoginAsStandardUserAsync();

        await Assertions.Expect(InventoryPage.Products).ToHaveCountAsync(TestConfig.ExpectedProductCount);

        await LoginPage.OpenHamburgerMenuAsync();
        await Assertions.Expect(LoginPage.MenuOverlay).ToBeVisibleAsync();
        await LoginPage.CloseHamburgerMenuAsync();
    }

    [Fact]
    [Trait("Scenario", "25-responsive-design")]
    public async Task The_shop_works_on_a_tablet_viewport()
    {
        await Page.SetViewportSizeAsync(TabletViewport.Width, TabletViewport.Height);

        await LoginAsStandardUserAsync();

        await Assertions.Expect(InventoryPage.Products).ToHaveCountAsync(TestConfig.ExpectedProductCount);
    }

    [Fact]
    [Trait("Scenario", "25-responsive-design")]
    public async Task A_viewport_resize_during_interaction_keeps_the_cart_correct()
    {
        await LoginAsStandardUserAsync();

        // Resize to mobile
        await Page.SetViewportSizeAsync(MobileViewport.Width, MobileViewport.Height);
        await InventoryPage.AddProductToCartAsync(0);

        // Resize to tablet
        await Page.SetViewportSizeAsync(TabletViewport.Width, TabletViewport.Height);
        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("1");
    }

    [Fact]
    [Trait("Scenario", "25-responsive-design")]
    public async Task Products_can_be_added_to_the_cart_on_a_mobile_viewport()
    {
        await Page.SetViewportSizeAsync(MobileViewport.Width, MobileViewport.Height);

        await LoginAsStandardUserAsync();

        await InventoryPage.AddProductToCartAsync(0);
        await InventoryPage.AddProductToCartAsync(1);

        await Assertions.Expect(InventoryPage.CartBadge).ToHaveTextAsync("2");
    }

    [Fact]
    [Trait("Scenario", "25-responsive-design")]
    public async Task Checkout_works_on_a_tablet_viewport()
    {
        await Page.SetViewportSizeAsync(TabletViewport.Width, TabletViewport.Height);

        await LoginAsStandardUserAsync();

        await InventoryPage.AddProductToCartAsync(0);
        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();

        await CartPage.CheckoutAsync();
        await CheckoutPage.WaitForPageLoadAsync();

        await CheckoutPage.FillInfoAsync("Test", "User", "12345");
        await CheckoutPage.ClickContinueAsync();

        await Assertions.Expect(CheckoutPage.FinishButton).ToBeVisibleAsync(VisibleWithin(Timeouts.Default));
    }

    private static (int Width, int Height) MobileViewport => (375, 667);

    private static (int Width, int Height) TabletViewport => (768, 1024);
}
