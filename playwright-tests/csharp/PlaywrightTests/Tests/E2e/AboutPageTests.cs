namespace PlaywrightTests.Tests.E2e;

/// <summary>
/// About page - navigating from the hamburger menu.
/// </summary>
public sealed class AboutPageTests
{
    /// <summary>
    /// SAUCE_DEMO_BUG: the About link leads to saucelabs.com, which returns
    /// 403 Forbidden. The redirect and the About page content cannot be verified,
    /// so the test is skipped, exactly as in the reference suites.
    ///
    /// Once the SauceDemo-side bug is fixed: make the class inherit
    /// <c>E2eTestBase</c>, add <c>ClickAboutAsync()</c> to <c>InventoryPage</c>
    /// (clicking the <c>about-sidebar-link</c>) and drop the Skip with the body:
    /// login -&gt; open the hamburger menu -&gt; click About -&gt; assert the
    /// saucelabs.com URL.
    /// </summary>
    [Fact(Skip = "SAUCE_DEMO_BUG: the About link leads to saucelabs.com, which returns 403 Forbidden, so the redirect cannot be verified.")]
    [Trait("Scenario", "24-about-page")]
    public Task About_page_navigation_from_the_hamburger_menu() => Task.CompletedTask;
}
