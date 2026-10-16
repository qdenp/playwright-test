namespace PlaywrightTests.Config;

/// <summary>
/// Named timeouts for the whole suite. Nothing is hardcoded in tests or page objects.
/// </summary>
public static class Timeouts
{
    /// <summary>Default timeout for waits and assertions on elements.</summary>
    public const int Default = 10_000;

    /// <summary>Navigations (page loads) are slower - they need their own budget.</summary>
    public const int Navigation = 30_000;

    /// <summary>performance_glitch_user is deliberately slow - it needs room to log in.</summary>
    public const int Slow = 60_000;

    /// <summary>Waiting for an error message on the login form.</summary>
    public const int Error = 3_000;

    /// <summary>Auto-retrying (web-first) assertion budget.</summary>
    public const int Assert = 5_000;

    /// <summary>
    /// Per-action budget. The demo stand throttles parallel connections, so
    /// actions get the room the reference suites give them (actionTimeout 60s).
    /// </summary>
    public const int Action = 60_000;
}
