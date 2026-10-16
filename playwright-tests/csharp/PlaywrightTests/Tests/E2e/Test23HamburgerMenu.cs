using Microsoft.Playwright;
using Xunit.Abstractions;
using PlaywrightTests.Infrastructure;

namespace PlaywrightTests.Tests.E2e;

/// <summary>Hamburger menu - opening and closing.</summary>
public sealed class Test23HamburgerMenu : E2eTestBase
{
    public Test23HamburgerMenu(ITestOutputHelper output) : base(output)
    {
    }

    [Fact]
    [Trait("Scenario", "23-hamburger-menu")]
    public async Task Hamburger_menu_can_be_opened_and_closed()
    {
        await LoginAsStandardUserAsync();

        // Open the menu
        await LoginPage.OpenHamburgerMenuAsync();
        await Assertions.Expect(LoginPage.CloseButton).ToBeVisibleAsync();

        // The menu has 5 items
        await Assertions.Expect(InventoryPage.MenuItems).ToHaveCountAsync(5);

        // The menu items are present
        await Assertions.Expect(InventoryPage.InventoryLink).ToBeVisibleAsync();
        await Assertions.Expect(InventoryPage.AboutLink).ToBeVisibleAsync();
        await Assertions.Expect(InventoryPage.LogoutLink).ToBeVisibleAsync();

        // Close the menu
        await LoginPage.CloseHamburgerMenuAsync();
        await Assertions.Expect(LoginPage.CloseButton).Not.ToBeVisibleAsync();
    }
}
