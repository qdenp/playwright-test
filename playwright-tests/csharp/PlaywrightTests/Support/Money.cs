using System.Globalization;
using System.Text.RegularExpressions;

namespace PlaywrightTests.Support;

/// <summary>
/// Money parsing for prices and for the checkout summary.
/// "$1,234.56" -> 1234.56
/// </summary>
public static partial class Money
{
    private static readonly Regex AmountRegex = AmountPattern();

    public static decimal Parse(string? text)
    {
        if (string.IsNullOrWhiteSpace(text))
        {
            throw new InvalidOperationException("No amount found.");
        }

        var match = AmountRegex.Match(text);
        if (!match.Success)
        {
            throw new InvalidOperationException($"Could not parse the amount from \"{text}\".");
        }

        return decimal.Parse(match.Groups["amount"].Value, NumberStyles.AllowThousands | NumberStyles.Number, CultureInfo.InvariantCulture);
    }

    public static List<decimal> ParseAll(IEnumerable<string> texts) => texts.Select(Parse).ToList();

    [GeneratedRegex(@"\$\s*(?<amount>[\d,]+\.\d{2})")]
    private static partial Regex AmountPattern();
}
