using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Locked out user sees the locked out error.</summary>
public sealed class LockedUserTests : E2eTestBase
{
    public LockedUserTests(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "03-locked-user")]
    public async Task Locked_out_user_sees_the_locked_out_error()
    {
        var lockedUser = TestConfig.User("locked_out_user");

        await LoginPage.GoToAsync();
        await LoginPage.LoginAsync(lockedUser.Username, lockedUser.Password);

        await Assertions.Expect(LoginPage.ErrorMessage).ToBeVisibleAsync();

        var errorMessage = await LoginPage.GetErrorMessageAsync();
        Assert.Contains("locked out", errorMessage, StringComparison.Ordinal);
    }
}
