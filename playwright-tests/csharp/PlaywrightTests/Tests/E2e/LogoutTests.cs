using System.Text.RegularExpressions;
using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Logout - signing out of the account.</summary>
public sealed class LogoutTests : E2eTestBase
{
    public LogoutTests(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "05-logout")]
    public async Task Logout_signs_out_of_the_account()
    {
        await LoginAsStandardUserAsync();

        await Assertions.Expect(Page).ToHaveURLAsync(new Regex("inventory\\.html"));

        // Open the hamburger menu and click Logout
        await LoginPage.OpenHamburgerMenuAsync();
        await InventoryPage.ClickLogoutAsync();

        // The SPA does not change the URL, but the login form comes back
        await Assertions.Expect(LoginPage.UsernameInput).ToBeVisibleAsync(VisibleWithin(Timeouts.Default));
        await Assertions.Expect(LoginPage.PasswordInput).ToBeVisibleAsync(VisibleWithin(Timeouts.Default));
        await Assertions.Expect(LoginPage.LoginButton).ToBeVisibleAsync(VisibleWithin(Timeouts.Default));
    }
}
