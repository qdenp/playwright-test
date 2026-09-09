using Practice.Core;
using Shouldly;

namespace Practice.Core.Tests;

public class BookingOverlapTests
{
    private static Booking B(int startHour, int endHour) =>
        new(new DateTime(2026, 9, 9, startHour, 0, 0), new DateTime(2026, 9, 9, endHour, 0, 0));

    [Fact]
    public void Overlapping_intervals_overlap()
    {
        BookingOverlap.Overlaps(B(10, 12), B(11, 13)).ShouldBeTrue();
    }

    [Fact]
    public void Disjoint_intervals_do_not_overlap()
    {
        BookingOverlap.Overlaps(B(10, 11), B(12, 13)).ShouldBeFalse();
    }

    // TODO (упражнение 1): дописать тест на смежные интервалы --
    // [10:00, 11:00) и [11:00, 12:00) пересекаться НЕ должны.
    // Тест должен упасть на текущей реализации. Это и есть дефект.
}
