using System.Text.RegularExpressions;
using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Successful login with the standard user.</summary>
public sealed class Test01Login : E2eTestBase
{
    public Test01Login(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "01-login")]
    public async Task Successful_login_with_the_standard_user()
    {
        await LoginAsStandardUserAsync();

        await Assertions.Expect(Page).ToHaveURLAsync(new Regex("inventory\\.html"));
        await Assertions.Expect(InventoryPage.HeaderTitle).ToHaveTextAsync("Products");
        await Assertions.Expect(InventoryPage.Products.First).ToBeVisibleAsync();
    }
}
