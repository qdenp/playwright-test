using Microsoft.Playwright;
using PlaywrightTests.Config;

namespace PlaywrightTests.Fixtures;

/// <summary>
/// Holds the shared Playwright instance so the browser can be closed when the
/// test process exits. Every test gets its own browser context (and page) from
/// this browser, so tests never share cookies or storage.
/// </summary>
internal static class SharedBrowser
{
    private static readonly Lazy<Task<IBrowser>> Lazy = new(CreateAsync, LazyThreadSafetyMode.ExecutionAndPublication);

    static SharedBrowser()
    {
        AppDomain.CurrentDomain.ProcessExit += (_, _) => Close();
    }

    internal static Task<IBrowser> InstanceAsync() => Lazy.Value;

    internal static bool Created => Lazy.IsValueCreated;

    private static async Task<IBrowser> CreateAsync()
    {
        var playwright = await Playwright.CreateAsync();
        return await playwright.Chromium.LaunchAsync(new BrowserTypeLaunchOptions
        {
            Headless = true,
            // The test host runs as root inside a container.
            Args = new[] { "--no-sandbox", "--disable-dev-shm-usage" },
        });
    }

    internal static void Close()
    {
        if (!Created)
        {
            return;
        }

        try
        {
            Lazy.Value.GetAwaiter().GetResult().CloseAsync().GetAwaiter().GetResult();
        }
        catch (Exception)
        {
            // Best effort only: the process is already shutting down.
        }
    }
}

/// <summary>
/// One browser context (and one page) per test. The context is the browser state
/// boundary: SauceDemo keeps the cart and the session in browser storage, so
/// sharing a context between tests would leak state from one test into the next.
/// </summary>
public sealed class PlaywrightFixture : IAsyncLifetime
{
    private IBrowserContext? _context;

    public IPage Page { get; private set; } = null!;

    public IBrowserContext Context => _context ?? throw new InvalidOperationException("The fixture is not initialised.");

    public async Task InitializeAsync()
    {
        var browser = await SharedBrowser.InstanceAsync();

        _context = await browser.NewContextAsync(new BrowserNewContextOptions
        {
            BaseURL = TestConfig.BaseUrl,
            ViewportSize = new ViewportSize
            {
                Width = TestConfig.ViewportWidth,
                Height = TestConfig.ViewportHeight,
            },
            // See TestConfig.IgnoreHttpsErrors: the network intercepts TLS with its
            // own CA, which Chromium does not pick up from the system store.
            IgnoreHTTPSErrors = TestConfig.IgnoreHttpsErrors,
        });

        Page = await _context.NewPageAsync();

        // Navigate to login page using relative path (like TS fixture)
        // This ensures baseURL is used correctly on GitHub Pages subpath
        await Page.GotoAsync("", new() { WaitUntil = WaitUntilState.Load });

        // Pre-populate localStorage with product catalog for GitHub Pages
        // (products are served from localStorage, not a server API)
        await Page.EvaluateAsync(@"
            (() => {
                let products = [
                    { id: 'sauce_labs_backpack', name: 'Sauce Labs Backpack', price: 29.99, description: 'carry.allTheThings()', image: 'images/saucelabs_backpack.jpg' },
                    { id: 'sauce_labs_bike_light', name: 'Sauce Labs Bike Light', price: 9.99, description: 'Water-resistant', image: 'images/sauce_labs_bike_light.jpg' },
                    { id: 'sauce_labs_fleece_jacket', name: 'Sauce Labs Fleece Jacket', price: 49.99, description: 'Stylish fleece', image: 'images/sauce_labs_fleece_jacket.jpg' },
                    { id: 'sauce_labs_light_bike', name: 'Sauce Labs Light Bike', price: 15.99, description: 'Lightweight', image: 'images/sauce_labs_light_bike.jpg' },
                    { id: 'sauce_labs_bolt_tee', name: 'Sauce Labs Bolt T-Shirt', price: 15.99, description: 'Full-sleeve', image: 'images/sauce_labs_bolt_tee.jpg' },
                    { id: 'sauce_labs_fast_bike', name: 'Sauce Labs Fast Bike', price: 53.99, description: 'Racing bicycle', image: 'images/sauce_labs_fast_bike.jpg' }
                ];
                let productData = JSON.stringify({ products: products });
                let users = ['standard_user', 'problem_user', 'performance_glitch_user', 'error_user', 'visual_user', 'locked_out_user'];
                for (let i = 0; i < users.length; i++) {
                    localStorage.setItem('saucedemo_custom_user_' + users[i], productData);
                }
            })();
        ");
        // Reload to pick up the localStorage
        await Page.ReloadAsync(new() { WaitUntil = WaitUntilState.Load });

        // The demo stand throttles parallel connections, so actions get the same
        // generous budget as the reference runs (`actionTimeout` in milliseconds).
        Page.SetDefaultTimeout(Timeouts.Action);
        Page.SetDefaultNavigationTimeout(Timeouts.Navigation);
    }

    public async Task DisposeAsync()
    {
        if (_context is not null)
        {
            await _context.DisposeAsync();
        }
    }
}
