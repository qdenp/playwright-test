using System.Diagnostics;
using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Performance budgets for page loads.</summary>
public sealed class Test27PerformanceBudget : E2eTestBase
{
    private const int MeasurementCount = 3;

    public Test27PerformanceBudget(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "27-performance-budget")]
    public async Task The_login_page_loads_within_two_seconds()
    {
        // Warmup — prime caches
        await Page.GotoAsync(TestConfig.BaseUrl);
        await Page.WaitForLoadStateAsync(LoadState.NetworkIdle);

        var loadTimes = new List<long>(MeasurementCount);
        for (var i = 0; i < MeasurementCount; i++)
        {
            var stopwatch = Stopwatch.StartNew();
            await Page.GotoAsync(TestConfig.BaseUrl);
            await Page.WaitForLoadStateAsync(LoadState.NetworkIdle);
            stopwatch.Stop();
            loadTimes.Add(stopwatch.ElapsedMilliseconds);
        }

        var avgLoadTime = loadTimes.Average();
        Output.WriteLine($"Login page avg load time: {avgLoadTime:F0}ms over {MeasurementCount} runs");
        AssertWithinBudget(PerformanceBudgets.LoginPage, avgLoadTime, "login page");
    }

    [Fact]
    [Trait("Scenario", "27-performance-budget")]
    public async Task The_inventory_page_loads_within_three_seconds_after_login()
    {
        // Warmup — re-login once to prime caches
        await LoginPage.GoToAsync();
        await LoginPage.LoginAsync();
        await InventoryPage.WaitForPageLoadAsync();

        var loadTimes = new List<long>(MeasurementCount);
        for (var i = 0; i < MeasurementCount; i++)
        {
            var stopwatch = Stopwatch.StartNew();
            await LoginPage.GoToAsync();
            await LoginPage.LoginAsync();
            await InventoryPage.WaitForPageLoadAsync();
            stopwatch.Stop();
            loadTimes.Add(stopwatch.ElapsedMilliseconds);
        }

        var avgLoadTime = loadTimes.Average();
        Output.WriteLine($"Inventory page avg load time: {avgLoadTime:F0}ms over {MeasurementCount} runs");
        AssertWithinBudget(PerformanceBudgets.InventoryPage, avgLoadTime, "inventory page");
    }

    [Fact]
    [Trait("Scenario", "27-performance-budget")]
    public async Task All_six_products_load_within_the_performance_budget()
    {
        // Warmup
        await LoginPage.GoToAsync();
        await LoginPage.LoginAsync();
        await InventoryPage.WaitForPageLoadAsync();

        var loadTimes = new List<long>(MeasurementCount);
        for (var i = 0; i < MeasurementCount; i++)
        {
            var stopwatch = Stopwatch.StartNew();
            await LoginPage.GoToAsync();
            await LoginPage.LoginAsync();
            await InventoryPage.WaitForPageLoadAsync();
            stopwatch.Stop();
            loadTimes.Add(stopwatch.ElapsedMilliseconds);
        }

        var avgLoadTime = loadTimes.Average();
        await Assertions.Expect(InventoryPage.Products).ToHaveCountAsync(TestConfig.ExpectedProductCount);
        Output.WriteLine($"All 6 products avg load time: {avgLoadTime:F0}ms over {MeasurementCount} runs");
        AssertWithinBudget(PerformanceBudgets.AllProducts, avgLoadTime, "all products");
    }

    [Fact]
    [Trait("Scenario", "27-performance-budget")]
    public async Task The_cart_page_loads_within_four_seconds()
    {
        // Warmup — add product, open cart, return to inventory
        await LoginPage.GoToAsync();
        await LoginPage.LoginAsync();
        await InventoryPage.WaitForPageLoadAsync();
        await InventoryPage.AddProductToCartAsync(0);
        await InventoryPage.OpenCartAsync();
        await CartPage.WaitForPageLoadAsync();
        await CartPage.ContinueShoppingAsync();
        await InventoryPage.WaitForPageLoadAsync();

        var loadTimes = new List<long>(MeasurementCount);
        for (var i = 0; i < MeasurementCount; i++)
        {
            var stopwatch = Stopwatch.StartNew();
            await InventoryPage.OpenCartAsync();
            await CartPage.WaitForPageLoadAsync();
            stopwatch.Stop();
            loadTimes.Add(stopwatch.ElapsedMilliseconds);
            // Navigate back to inventory for the next iteration
            await CartPage.ContinueShoppingAsync();
            await InventoryPage.WaitForPageLoadAsync();
        }

        var avgLoadTime = loadTimes.Average();
        Output.WriteLine($"Cart page avg load time: {avgLoadTime:F0}ms over {MeasurementCount} runs");
        AssertWithinBudget(PerformanceBudgets.CartPage, avgLoadTime, "cart page");
    }

    [Fact]
    [Trait("Scenario", "27-performance-budget")]
    public async Task The_average_load_time_of_three_runs_stays_within_the_budget()
    {
        // Warmup — login once to prime caches
        await LoginPage.GoToAsync();
        await LoginPage.LoginAsync();
        await InventoryPage.WaitForPageLoadAsync();

        var loadTimes = new List<long>(MeasurementCount);
        for (var i = 0; i < MeasurementCount; i++)
        {
            var stopwatch = Stopwatch.StartNew();
            await LoginPage.GoToAsync();
            await LoginPage.LoginAsync();
            await InventoryPage.WaitForPageLoadAsync();
            stopwatch.Stop();
            loadTimes.Add(stopwatch.ElapsedMilliseconds);
        }

        var avgLoadTime = loadTimes.Average();
        Output.WriteLine($"Load times: {string.Join(", ", loadTimes.Select(t => $"{t}ms"))}, average {avgLoadTime:F0}ms");
        AssertWithinBudget(PerformanceBudgets.AverageLoad, avgLoadTime, "average load time");
    }

    [Fact]
    [Trait("Scenario", "27-performance-budget")]
    public async Task Product_images_load_within_budget()
    {
        // Warmup
        await LoginPage.GoToAsync();
        await LoginPage.LoginAsync();
        await InventoryPage.WaitForPageLoadAsync();
        await InventoryPage.WaitForFirstProductImageAsync();

        var loadTimes = new List<long>(MeasurementCount);
        for (var i = 0; i < MeasurementCount; i++)
        {
            var stopwatch = Stopwatch.StartNew();
            await LoginPage.GoToAsync();
            await LoginPage.LoginAsync();
            await InventoryPage.WaitForPageLoadAsync();
            await InventoryPage.WaitForFirstProductImageAsync();
            stopwatch.Stop();
            loadTimes.Add(stopwatch.ElapsedMilliseconds);
        }

        var avgLoadTime = loadTimes.Average();
        Output.WriteLine($"Images avg load time: {avgLoadTime:F0}ms over {MeasurementCount} runs");
        AssertWithinBudget(PerformanceBudgets.ProductImages, avgLoadTime, "product images");
    }

    private static void AssertWithinBudget(double budgetMs, double elapsedMs, string what)
    {
        Assert.True(
            elapsedMs < budgetMs,
            $"{what} took {elapsedMs:F0}ms, which is over the budget of {budgetMs}ms.");
    }
}
