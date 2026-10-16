namespace PlaywrightTests.Config;

/// <summary>
/// Performance budgets for the load-time tests, in milliseconds.
/// </summary>
public static class PerformanceBudgets
{
    public const int LoginPage = 3_000;
    public const int InventoryPage = 3_000;
    public const int AllProducts = 5_000;
    public const int CartPage = 4_000;
    public const int AverageLoad = 5_000;
    public const int ProductImages = 3_000;
}
