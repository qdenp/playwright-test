using Microsoft.Playwright;
using PlaywrightTests.Config;
using PlaywrightTests.Fixtures;
using PlaywrightTests.PageObjects;
using Xunit.Abstractions;

namespace PlaywrightTests.Infrastructure;

/// <summary>
/// Base class for every e2e test. xUnit creates a new instance of the test class
/// for every test method, so each test gets its own browser context and page -
/// no test depends on the state of another one.
/// </summary>
public abstract class E2eTestBase : IAsyncLifetime
{
    private PlaywrightFixture _fixture = null!;

    protected E2eTestBase(ITestOutputHelper output)
    {
        Output = output;
    }

    protected ITestOutputHelper Output { get; }

    protected IPage Page { get; private set; } = null!;

    protected LoginPage LoginPage { get; private set; } = null!;

    protected InventoryPage InventoryPage { get; private set; } = null!;

    protected CartPage CartPage { get; private set; } = null!;

    protected CheckoutPage CheckoutPage { get; private set; } = null!;

    public async Task InitializeAsync()
    {
        // A fresh context per test: the cart and the login session live in
        // browser storage, which a shared context would carry over.
        _fixture = new PlaywrightFixture();
        await _fixture.InitializeAsync();

        Page = _fixture.Page;
        LoginPage = new LoginPage(Page);
        InventoryPage = new InventoryPage(Page);
        CartPage = new CartPage(Page);
        CheckoutPage = new CheckoutPage(Page);
    }

    public async Task DisposeAsync() => await _fixture.DisposeAsync();

    /// <summary>
    /// Mirrors the reference "authenticatedPage" fixture: open the login page,
    /// log in with the configured account and wait for the catalogue to render.
    /// Every test performs its own login this way.
    /// </summary>
    protected async Task LoginAsStandardUserAsync()
    {
        // Fixture already navigated to login page and reloaded.
        // Just login — no need to navigate again.
        await LoginPage.LoginAsync();
        await WaitForCatalogueLoadedAsync();
    }

    protected async Task LoginAsAsync(string username, string password)
    {
        await LoginPage.GoToAsync();
        await LoginPage.LoginAsync(username, password);
        await WaitForCatalogueLoadedAsync();
    }

    /// <summary>
    /// The catalogue is on screen once the header is visible and all products are
    /// rendered. Waiting for both keeps the following assertions web-first.
    /// </summary>
    private async Task WaitForCatalogueLoadedAsync()
    {
        await InventoryPage.WaitForPageLoadAsync();
        await Assertions.Expect(InventoryPage.Products)
            .ToHaveCountAsync(TestConfig.ExpectedProductCount, new LocatorAssertionsToHaveCountOptions { Timeout = Timeouts.Navigation });
    }

    /// <summary>
    /// Money comparison: both amounts are rounded to cents before comparing.
    /// </summary>
    protected static void AssertAmountEqual(decimal expected, decimal actual)
    {
        Assert.True(
            Math.Round(expected, 2) == Math.Round(actual, 2),
            $"Expected amount {expected:F2} but got {actual:F2}.");
    }

    /// <summary>
    /// Visibility assertion with an explicit, named timeout.
    /// </summary>
    protected static LocatorAssertionsToBeVisibleOptions VisibleWithin(int timeoutMs) => new() { Timeout = timeoutMs };
}
