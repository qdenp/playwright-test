using System.Text.RegularExpressions;
using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Config;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Social links at the bottom of the screen (X, Facebook, LinkedIn).</summary>
public sealed class Test29SocialMediaLinks : E2eTestBase
{
    public Test29SocialMediaLinks(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "29-social-media-links")]
    public async Task All_three_social_links_have_the_expected_targets()
    {
        await LoginAsStandardUserAsync();

        await Assertions.Expect(InventoryPage.SocialXLink)
            .ToHaveAttributeAsync("href", "https://x.com/saucelabs");
        await Assertions.Expect(InventoryPage.SocialFacebookLink)
            .ToHaveAttributeAsync("href", "https://www.facebook.com/saucelabs");
        await Assertions.Expect(InventoryPage.SocialLinkedinLink)
            .ToHaveAttributeAsync("href", "https://www.linkedin.com/company/sauce-labs/");
    }

    [Fact]
    [Trait("Scenario", "29-social-media-links")]
    public async Task Every_social_link_opens_in_a_new_tab()
    {
        await LoginAsStandardUserAsync();

        foreach (var link in new[]
                 {
                     InventoryPage.SocialXLink,
                     InventoryPage.SocialFacebookLink,
                     InventoryPage.SocialLinkedinLink,
                 })
        {
            await Assertions.Expect(link).ToHaveAttributeAsync("target", "_blank");
            await Assertions.Expect(link).ToHaveAttributeAsync("rel", new Regex("noreferrer"));
        }

        // Follow the X link and confirm it leaves the shop
        var popup = await Page.RunAndWaitForPopupAsync(
            async () => await InventoryPage.SocialXLink.ClickAsync());
        await Assertions.Expect(popup).ToHaveURLAsync(new Regex("x\\.com"));
    }

    [Fact]
    [Trait("Scenario", "29-social-media-links")]
    public async Task The_shop_stays_usable_after_a_social_link_was_opened()
    {
        await LoginAsStandardUserAsync();

        var popup = await Page.RunAndWaitForPopupAsync(
            async () => await InventoryPage.SocialFacebookLink.ClickAsync());
        await popup.CloseAsync();

        // The shop is untouched and still holds the products
        await Assertions.Expect(Page).ToHaveURLAsync(new Regex("inventory\\.html"));
        await Assertions.Expect(InventoryPage.Products).ToHaveCountAsync(TestConfig.ExpectedProductCount);
    }
}
