# SauceDemo Local — GitHub Pages

Скопированная версия [SauceDemo](https://www.saucedemo.com) для автономного тестирования.

## Что внутри

- Полностью статический React SPA (Vite build)
- Клиентская генерация PDF (`@react-pdf/renderer`)
- localStorage для корзины и авторизации
- 29 e2e-тестов (TypeScript, JavaScript, Python, C#, Java)

## Деплой на GitHub Pages

1. Создайте репозиторий (например, `username/saucedemo-local`)
2. Добавьте этот репозиторий как сабмодуль:
   ```bash
   git submodule add https://github.com/username/saucedemo-local.git saucedemo-pages
   ```
   ИЛИ просто скопируйте файлы.
3. В настройках репозитория: **Settings → Pages → Source: Deploy from a branch**
4. Branch: `main`, folder: `/ (root)`
5. Или просто push в корень репозитория

## Автоматический деплой

GitHub Actions настроен в `.github/workflows/deploy.yml` — деплой происходит автоматически при push в `main`.

## Локальный запуск

```bash
python -m http.server 3000 --directory saucedemo-pages
```

Или используйте Node.js сервер из проекта:
```bash
node saucedemo-server.js
```

Открыть: http://localhost:3000

## Тесты

Тесты находятся в `playwright-tests/` и настроены на `http://localhost:3000`.

```bash
# TypeScript
cd playwright-tests/typescript
npx playwright test --project=chromium

# JavaScript
cd ../js
npx playwright test --project=chromium

# Python
cd ../python
pytest

# C#
cd ../csharp/PlaywrightTests
dotnet test

# Java
cd ../java
mvn test
```
