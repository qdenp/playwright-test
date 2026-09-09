using Practice.Core;
using Shouldly;

namespace Practice.Core.Tests;

public class DurationFormatterTests
{
    [Theory]
    [InlineData(0, "0s")]
    [InlineData(45, "45s")]
    [InlineData(60, "1m")]
    [InlineData(65, "1m 5s")]
    [InlineData(3600, "1h")]
    [InlineData(5430, "1h 30m 30s")]
    [InlineData(90000, "1d 1h")]
    public void Formats_duration(int totalSeconds, string expected)
    {
        DurationFormatter.Format(TimeSpan.FromSeconds(totalSeconds)).ShouldBe(expected);
    }

    [Fact]
    public void Negative_duration_is_rejected()
    {
        Should.Throw<ArgumentOutOfRangeException>(
            () => DurationFormatter.Format(TimeSpan.FromSeconds(-1)));
    }
}
