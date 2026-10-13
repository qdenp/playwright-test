using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>All users see the same catalogue.</summary>
public sealed class MultipleUsersCatalogTests : E2eTestBase
{
    public MultipleUsersCatalogTests(ITestOutputHelper output) : base(output)
    {
    }

    private static readonly List<UserCredentials> CatalogUsers = new()
    {
        new("standard_user", "secret_sauce"),
        new("problem_user", "secret_sauce"),
        new("performance_glitch_user", "secret_sauce"),
        new("error_user", "secret_sauce"),
        new("visual_user", "secret_sauce"),
    };

    [Fact]
    [Trait("Scenario", "10-multiple-users-catalog")]
    public async Task All_users_see_the_same_catalogue()
    {
        List<string>? baselineNames = null;

        foreach (var user in CatalogUsers)
        {
            await LoginAsAsync(user.Username, user.Password);
            await InventoryPage.WaitForPageLoadAsync();

            // All users see the same number of named products
            await Assertions.Expect(InventoryPage.ProductNames).ToHaveCountAsync(TestConfig.ExpectedProductCount);

            var names = await InventoryPage.GetAllProductNamesAsync();

            if (baselineNames is null)
            {
                baselineNames = names;
            }
            else
            {
                Assert.Equal(baselineNames, names);
            }
        }
    }
}
