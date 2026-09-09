namespace Practice.Core;

/// <summary>
/// Проверка пересечения интервалов бронирования переводчика.
/// СПЕЦИФИКАЦИЯ:
///   - интервал задаётся [Start, End), то есть End не входит в интервал;
///   - два интервала пересекаются, если у них есть хотя бы одна общая точка;
///   - смежные интервалы (End одного равен Start другого) НЕ пересекаются;
///   - интервал с Start больше End некорректен -> ArgumentException.
/// В этой реализации есть дефект. Найди его и докажи тестом.
/// </summary>
public sealed record Booking(DateTime Start, DateTime End);

public static class BookingOverlap
{
    public static bool Overlaps(Booking a, Booking b)
    {
        if (a.Start > a.End) throw new ArgumentException("Некорректный интервал", nameof(a));
        if (b.Start > b.End) throw new ArgumentException("Некорректный интервал", nameof(b));

        return a.Start <= b.End && b.Start <= a.End;
    }
}
