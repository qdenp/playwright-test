---
description: Запуск Python-тестов pytest с разбором падений
---

Запусти Python-набор SauceDemo.

Параметры: аргументы из `$ARGUMENTS` добавляй к команде `pytest` в конце
- без аргументов — весь набор
- путь до файла, например `tests/e2e/test-06-full-purchase-flow.py`
- `-k`, `-x` — передаются в pytest как есть

Команда:
```powershell
cd playwright-tests/python
pytest
```

`--reporter=line` использовать НЕЛЬЗЯ: в проекте `pytest-html`, а не `pytest-reporter`.
Флаги `-v`, `--html`, `--reruns 3`, `--reruns-delay 1` уже заданы в `pytest.ini`.

После запуска:

1. Выведи итоговую сводку: passed / failed / rerun / skipped.
2. Для каждого упавшего теста — причина, `assert` или `expect`, файл и строка.
3. Не исправляй ничего без отдельного запроса. Сначала — диагностика.
4. Учитывай, что в `pytest.ini` включены `--reruns 3 --reruns-delay 1`: тест,
   упавший и позже пройденный, — это flaky, а не зелёный.
