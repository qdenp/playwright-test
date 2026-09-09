namespace Practice.Core;

/// <summary>
/// УПРАЖНЕНИЕ 2: реализовать самому, РУКАМИ, без агента.
///
/// Format(TimeSpan) возвращает длительность звонка в человекочитаемом виде:
///   0 сек          -> "0s"
///   45 сек         -> "45s"
///   60 сек         -> "1m"
///   65 сек         -> "1m 5s"
///   3600 сек       -> "1h"
///   5430 сек       -> "1h 30m 30s"
///   90000 сек      -> "1d 1h"
///   отрицательное  -> ArgumentOutOfRangeException
///
/// Правила: нулевые компоненты не выводятся, кроме случая нулевой длительности;
/// компоненты разделяются одним пробелом; порядок d, h, m, s.
/// </summary>
public static class DurationFormatter
{
    public static string Format(TimeSpan duration)
    {
        throw new NotImplementedException();
    }
}
