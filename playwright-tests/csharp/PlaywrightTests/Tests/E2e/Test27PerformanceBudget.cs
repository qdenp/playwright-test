using System.Diagnostics;
using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Performance budgets for page loads.</summary>
public sealed class Test27PerformanceBudget : E2eTestBase
{
    public Test27PerformanceBudget(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "27-performance-budget")]
    public async Task The_login_page_loads_within_two_seconds()
    {
        var stopwatch = Stopwatch.StartNew();

        await Page.GotoAsync(TestConfig.BaseUrl);

        stopwatch.Stop();
        Output.WriteLine($"Login page loaded in {stopwatch.ElapsedMilliseconds}ms");

        AssertWithinBudget(PerformanceBudgets.LoginPage, stopwatch.ElapsedMilliseconds, "login page");
    }

    [Fact]
    [Trait("Scenario", "27-performance-budget")]
    public async Task The_inventory_page_loads_within_three_seconds_after_login()
    {
        var stopwatch = Stopwatch.StartNew();

        await LoginPage.GoToAsync();
        await LoginPage.LoginAsync();
        await InventoryPage.WaitForPageLoadAsync();

        stopwatch.Stop();
        Output.WriteLine($"Inventory page loaded in {stopwatch.ElapsedMilliseconds}ms");

        AssertWithinBudget(PerformanceBudgets.InventoryPage, stopwatch.ElapsedMilliseconds, "inventory page");
    }

    [Fact]
    [Trait("Scenario", "27-performance-budget")]
    public async Task All_six_products_load_within_the_performance_budget()
    {
        var stopwatch = Stopwatch.StartNew();

        await LoginPage.GoToAsync();
        await LoginPage.LoginAsync();
        await InventoryPage.WaitForPageLoadAsync();

        stopwatch.Stop();
        Output.WriteLine($"All 6 products loaded in {stopwatch.ElapsedMilliseconds}ms");

        await Assertions.Expect(InventoryPage.Products).ToHaveCountAsync(TestConfig.ExpectedProductCount);

        AssertWithinBudget(PerformanceBudgets.AllProducts, stopwatch.ElapsedMilliseconds, "all products");
    }

    [Fact]
    [Trait("Scenario", "27-performance-budget")]
    public async Task The_cart_page_loads_within_four_seconds()
    {
        await LoginPage.GoToAsync();
        await LoginPage.LoginAsync();
        await InventoryPage.WaitForPageLoadAsync();

        var stopwatch = Stopwatch.StartNew();

        await InventoryPage.AddProductToCartAsync(0);
        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();

        stopwatch.Stop();
        Output.WriteLine($"Cart page loaded in {stopwatch.ElapsedMilliseconds}ms");

        AssertWithinBudget(PerformanceBudgets.CartPage, stopwatch.ElapsedMilliseconds, "cart page");
    }

    [Fact]
    [Trait("Scenario", "27-performance-budget")]
    public async Task The_average_load_time_of_three_runs_stays_within_the_budget()
    {
        await LoginPage.GoToAsync();
        await LoginPage.LoginAsync();

        var loadTimes = new List<long>(3);

        for (var i = 0; i < 3; i++)
        {
            var stopwatch = Stopwatch.StartNew();

            // The login above acts as the warm-up: it is not part of the average.
            await InventoryPage.WaitForPageLoadAsync();
            stopwatch.Stop();
            loadTimes.Add(stopwatch.ElapsedMilliseconds);
        }

        var averageLoadTime = loadTimes.Average();

        Output.WriteLine($"Load times: {string.Join(", ", loadTimes.Select(time => $"{time}ms"))}, average {averageLoadTime:F0}ms");

        AssertWithinBudget(PerformanceBudgets.AverageLoad, averageLoadTime, "average load time");
    }

    [Fact]
    [Trait("Scenario", "27-performance-budget")]
    public async Task Product_images_load_within_budget()
    {
        await LoginAsStandardUserAsync();

        var stopwatch = Stopwatch.StartNew();

        await InventoryPage.WaitForFirstProductImageAsync();

        stopwatch.Stop();
        Output.WriteLine($"Images loaded in {stopwatch.ElapsedMilliseconds}ms");

        AssertWithinBudget(PerformanceBudgets.ProductImages, stopwatch.ElapsedMilliseconds, "product images");
    }

    private static void AssertWithinBudget(int budgetMs, double elapsedMs, string what)
    {
        Assert.True(
            elapsedMs < budgetMs,
            $"{what} took {elapsedMs:F0}ms, which is over the budget of {budgetMs}ms.");
    }
}
