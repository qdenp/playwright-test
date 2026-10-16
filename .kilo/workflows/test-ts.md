---
description: Запуск TypeScript-тестов Playwright (chromium) с разбором падений
---

Запусти TypeScript-набор SauceDemo на chromium.

Параметры (используй `$ARGUMENTS`):
- без аргументов — весь набор
- имя файла или путь, например `04-add-to-cart` или `tests/e2e/05-sort-by-price.spec.ts`
- `--grep`, `--project=firefox|webkit` — передаются в playwright как есть

Команда:
```powershell
cd playwright-tests/typescript
npx playwright test --project=chromium --reporter=line $ARGUMENTS
```

После запуска:

1. Выведи итоговую сводку: сколько passed / failed / flaky / skipped.
2. Для каждого упавшего теста — причина из вывода, конкретный файл и строка.
3. Не исправляй ничего без отдельного запроса. Сначала — диагностика.
4. Если падений нет, так и скажи одной строкой.
