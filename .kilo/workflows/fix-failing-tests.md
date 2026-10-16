---
description: Прогон всех трёх наборов с фиксом падений до зелёного
---

Приведи тесты к зелёному состоянию, только после того как увидишь реальные падения.

Шаги:

1. TypeScript:
```powershell
cd playwright-tests/typescript
npx tsc --noEmit
npx playwright test --project=chromium --reporter=line
```
2. JavaScript:
```powershell
cd playwright-tests/js
npx playwright test --project=chromium --reporter=line
```
3. Python:
```powershell
cd playwright-tests/python
pytest --reporter=line
```

Правила работы с падениями:

- Сначала полностью собери список упавших тестов, потом правь. Не чини по одному наугад.
- Для каждого падения найди корневую причину (локатор, таймаут, состояние теста, реальный баг
  приложения), а не симптом.
- Имена файлов, которые стоит проверить в первую очередь: `13-add-remove-all.spec.ts` и
  `18-about-page.spec.ts` — они ломают `npx tsc --noEmit` уже сейчас.
- После каждой правки перезапускай релевантный набор.
- Правка в одном наборе = правка в остальных двух, либо фиксация расхождения в скиле
  `playwright-tests` (таблица «Известное состояние»).
- `npx tsc --noEmit` в `typescript/` обязан быть чистым: джоба `lint` в CI падает на ошибках типов.
- Не сообщай «всё зелено», пока сам не увидел `passed` в выводе всех трёх прогонов.
- В выводе укажи: команду, фактический результат, список затронутых файлов с `file:line`.
