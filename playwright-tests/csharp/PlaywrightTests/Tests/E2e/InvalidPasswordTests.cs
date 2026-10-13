using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Invalid password shows an authentication error.</summary>
public sealed class InvalidPasswordTests : E2eTestBase
{
    public InvalidPasswordTests(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "02-invalid-password")]
    public async Task Invalid_password_shows_an_authentication_error()
    {
        await LoginPage.GoToAsync();
        await LoginPage.LoginAsync(TestConfig.Username, "wrong_password");

        await Assertions.Expect(LoginPage.ErrorMessage).ToBeVisibleAsync();

        var errorMessage = await LoginPage.GetErrorMessageAsync();
        Assert.Contains("do not match", errorMessage, StringComparison.Ordinal);
    }
}
