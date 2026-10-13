using Microsoft.Playwright;
using PlaywrightTests.Config;

namespace PlaywrightTests.PageObjects;

/// <summary>
/// Page Object for the login page.
/// </summary>
public sealed class LoginPage
{
    private readonly IPage _page;

    public LoginPage(IPage page)
    {
        _page = page;
    }

    public ILocator UsernameInput => _page.Locator("#user-name");

    public ILocator PasswordInput => _page.Locator("#password");

    public ILocator LoginButton => _page.Locator("#login-button");

    public ILocator ErrorMessage => _page.Locator(".error-message-container");

    public ILocator HamburgerButton => _page.Locator(".bm-burger-button");

    public ILocator CloseButton => _page.Locator(".bm-cross-button");

    public ILocator MenuOverlay => _page.Locator(".bm-menu-wrap");

    public async Task GoToAsync()
    {
        await _page.GotoAsync(
            TestConfig.BaseUrl,
            new PageGotoOptions
            {
                WaitUntil = WaitUntilState.DOMContentLoaded,
                Timeout = Timeouts.Navigation,
            });
    }

    public async Task EnterUsernameAsync(string username) => await UsernameInput.FillAsync(username);

    public async Task EnterPasswordAsync(string password) => await PasswordInput.FillAsync(password);

    public async Task ClickLoginAsync() => await LoginButton.ClickAsync();

    /// <summary>
    /// Login with the configured credentials by default.
    /// An empty string is valid input (the "empty username" case), so only a null
    /// argument falls back to the configuration.
    /// </summary>
    public Task LoginAsync() => LoginAsync(username: null, password: null);

    public async Task LoginAsync(string? username, string? password)
    {
        await EnterUsernameAsync(username ?? TestConfig.Username);
        await EnterPasswordAsync(password ?? TestConfig.Password);
        await ClickLoginAsync();
    }

    public async Task<string> GetErrorMessageAsync() => (await ErrorMessage.InnerTextAsync()).Trim();

    public async Task OpenHamburgerMenuAsync() => await HamburgerButton.ClickAsync();

    public async Task CloseHamburgerMenuAsync() => await CloseButton.ClickAsync();
}
