using PlaywrightTests.Config;

namespace PlaywrightTests.Config;

/// <summary>
/// A SauceDemo account. Passwords come from the environment with the
/// public demo value as fallback ( SauceDemo is a public demo stand).
/// </summary>
public sealed record UserCredentials(string Username, string Password);

/// <summary>
/// One row of the parameterised login test.
/// </summary>
public sealed record LoginTestCase(
    string Username,
    string Password,
    bool ShouldLogin,
    string ExpectedTitle,
    string? ExpectedError)
{
    public override string ToString()
    {
        var user = string.IsNullOrEmpty(Username) ? "<empty>" : Username;
        var result = ShouldLogin ? "success" : "failure";
        return $"{user} / {result}";
    }
}

/// <summary>
/// Test configuration, read from the environment once at start-up.
/// </summary>
public static class TestConfig
{
    private static string Env(string name, string fallback)
    {
        var value = Environment.GetEnvironmentVariable(name);
        return string.IsNullOrEmpty(value) ? fallback : value;
    }

    public static string Username => Env("SAUCE_DEMO_USERNAME", "standard_user");

    public static string Password => Env("SAUCE_DEMO_PASSWORD", "secret_sauce");

    public static string BaseUrl => Env("SAUCE_DEMO_BASE_URL", "https://www.saucedemo.com").TrimEnd('/');

    /// <summary>Absolute URL of a page of the shop, e.g. Url("/cart.html").</summary>
    public static string Url(string relativePath) => $"{BaseUrl}/{relativePath.TrimStart('/')}";

    /// <summary>Number of products on the inventory page.</summary>
    public const int ExpectedProductCount = 6;

    /// <summary>Checkout tax rate charged by SauceDemo (8% of the item total).</summary>
    public const decimal CheckoutTaxRate = 0.08m;

    /// <summary>Desktop Chrome viewport, matching the reference runs.</summary>
    public const int ViewportWidth = 1280;

    public const int ViewportHeight = 720;

    /// <summary>
    /// TLS interception. The browser does not read the system CA bundle, so on a
    /// network that terminates TLS with its own CA (as this environment's
    /// Cloudflare proxy does) every navigation fails with
    /// ERR_CERT_AUTHORITY_INVALID. Chromium is therefore started with
    /// IgnoreHTTPSErrors. Set SAUCE_DEMO_IGNORE_HTTPS_ERRORS=false on a network
    /// without a TLS-intercepting proxy to get strict verification back.
    /// </summary>
    public static bool IgnoreHttpsErrors =>
        !string.Equals(Env("SAUCE_DEMO_IGNORE_HTTPS_ERRORS", "true"), "false", StringComparison.OrdinalIgnoreCase);

    /// <summary>All accounts of the demo stand.</summary>
    public static IReadOnlyList<UserCredentials> Users { get; } = new List<UserCredentials>
    {
        new("standard_user", Env("SAUCE_DEMO_PASSWORD_STANDARD", "secret_sauce")),
        new("problem_user", Env("SAUCE_DEMO_PASSWORD_PROBLEM", "secret_sauce")),
        new("performance_glitch_user", Env("SAUCE_DEMO_PASSWORD_PERFORMANCE", "secret_sauce")),
        new("error_user", Env("SAUCE_DEMO_PASSWORD_ERROR", "secret_sauce")),
        new("visual_user", Env("SAUCE_DEMO_PASSWORD_VISUAL", "secret_sauce")),
        new("locked_out_user", Env("SAUCE_DEMO_PASSWORD_LOCKED", "secret_sauce")),
    };

    public static UserCredentials User(string username)
    {
        var user = Users.FirstOrDefault(u => u.Username == username);
        if (user is null)
        {
            throw new InvalidOperationException($"Unknown SauceDemo account \"{username}\".");
        }

        return user;
    }

    /// <summary>Rows of the parameterised login test.</summary>
    public static IReadOnlyList<LoginTestCase> LoginTestCases { get; } = new List<LoginTestCase>
    {
        new("standard_user", "secret_sauce", true, "Products", null),
        new("problem_user", "secret_sauce", true, "Products", null),
        new("performance_glitch_user", "secret_sauce", true, "Products", null),
        new("error_user", "secret_sauce", true, "Products", null),
        new("visual_user", "secret_sauce", true, "Products", null),
        new("locked_out_user", "secret_sauce", false, "Products", "locked out"),
        new("standard_user", "wrong_password", false, "Products", "Username and password do not match"),
        new(string.Empty, "secret_sauce", false, "Products", "Username is required"),
    };
}
