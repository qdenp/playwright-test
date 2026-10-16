using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Every account of the demo stand logs in (or fails to) as expected.</summary>
public sealed class Test04ParameterizedLogin : E2eTestBase
{
    public Test04ParameterizedLogin(ITestOutputHelper output) : base(output)
    {
    }

    public static TheoryData<LoginTestCase> LoginCases => new(TestConfig.LoginTestCases);

    [Theory]
    [MemberData(nameof(LoginCases))]
    [Trait("Scenario", "04-parameterized-login")]
    public async Task Login_with_an_account_shows_the_expected_outcome(LoginTestCase testCase)
    {
        await LoginPage.GoToAsync();

        if (testCase.ShouldLogin)
        {
            await LoginPage.LoginAsync(testCase.Username, testCase.Password);

            // A successful login leaves the login form behind and shows the catalogue.
            // performance_glitch_user needs the longer budget.
            await Assertions.Expect(InventoryPage.HeaderTitle).ToHaveTextAsync(
                testCase.ExpectedTitle,
                new LocatorAssertionsToHaveTextOptions { Timeout = Timeouts.Slow });
        }
        else
        {
            // A failed login stays on the login form and explains why.
            await LoginPage.LoginAsync(testCase.Username, testCase.Password);

            await Assertions.Expect(LoginPage.UsernameInput).ToBeVisibleAsync();
            await Assertions.Expect(LoginPage.ErrorMessage).ToContainTextAsync(testCase.ExpectedError!);
            await Assertions.Expect(InventoryPage.HeaderTitle).ToBeHiddenAsync();
        }
    }
}
