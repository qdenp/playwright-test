using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>All users see the same catalogue.</summary>
public sealed class Test10MultipleUsersCatalog : E2eTestBase
{
    public Test10MultipleUsersCatalog(ITestOutputHelper output) : base(output)
    {
    }

    private static IReadOnlyList<UserCredentials> CatalogUsers =>
        TestConfig.Users.Take(5).ToList();

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
