# Environment Variables для тестов

## Переменные

### Основные
| Переменная | Описание | Значение по умолчанию |
|------------|----------|----------------------|
| `SAUCE_DEMO_USERNAME` | Логин для авторизации | `standard_user` |
| `SAUCE_DEMO_PASSWORD` | Пароль для авторизации | `secret_sauce` |
| `SAUCE_DEMO_BASE_URL` | URL Sauce Demo | `https://www.saucedemo.com` |

### Все пользователи (через запятую)
| Переменная | Описание | Значение по умолчанию |
|------------|----------|----------------------|
| `SAUCE_DEMO_PASSWORDS` | Все пользователи и пароли | `standard_user:secret_sauce,problem_user:secret_sauce,performance_glitch_user:secret_sauce,error_user:secret_sauce,visual_user:secret_sauce,locked_out_user:secret_sauce` |

### Индивидуальные пароли (опционально)
| Переменная | Описание | Значение по умолчанию |
|------------|----------|----------------------|
| `SAUCE_DEMO_PASSWORD_STANDARD` | Пароль standard_user | `secret_sauce` |
| `SAUCE_DEMO_PASSWORD_PROBLEM` | Пароль problem_user | `secret_sauce` |
| `SAUCE_DEMO_PASSWORD_PERFORMANCE` | Пароль performance_glitch_user | `secret_sauce` |
| `SAUCE_DEMO_PASSWORD_ERROR` | Пароль error_user | `secret_sauce` |
| `SAUCE_DEMO_PASSWORD_VISUAL` | Пароль visual_user | `secret_sauce` |
| `SAUCE_DEMO_PASSWORD_LOCKED` | Пароль locked_out_user | `secret_sauce` |

## Использование

### Python (pytest)

```powershell
# Установка переменных
$env:SAUCE_DEMO_USERNAME="standard_user"
$env:SAUCE_DEMO_PASSWORD="secret_sauce"

# Запуск тестов
python -m pytest tests/e2e/ -v --browser=chromium
```

### JavaScript (Playwright)

```powershell
# Установка переменных
$env:SAUCE_DEMO_USERNAME="standard_user"
$env:SAUCE_DEMO_PASSWORD="secret_sauce"

# Запуск тестов
npx playwright test tests/e2e/
```

## Параметризованные тесты

### Python
Тест `test_08_parameterized_login.py` автоматически читает credentials из `SAUCE_DEMO_PASSWORDS`.

### JavaScript
Тест `08-parameterized-login.spec.js` автоматически читает credentials из `SAUCE_DEMO_PASSWORDS`.

## Что захардкожено

Только **invalid password test** использует hardcoded wrong password — это корректно, так как тестирует конкретный сценарий неверного пароля:
- `standard_user` + `wrong_password` → ошибка "do not match"
- пустой логин + `secret_sauce` → ошибка "required" (не работает в Sauce Demo)

## Page Object

LoginPage теперь принимает credentials из environment variables по умолчанию:

**Python:**
```python
login_page.login()  # Использует SAUCE_DEMO_USERNAME и SAUCE_DEMO_PASSWORD
login_page.login('other_user', 'other_password')  # Переопределяет
```

**JavaScript:**
```javascript
await loginPage.login();  // Использует SAUCE_DEMO_USERNAME и SAUCE_DEMO_PASSWORD
await loginPage.login('other_user', 'other_password');  // Переопределяет
```

## Функции для работы с credentials

**Python:**
```python
from tests.page_objects.login_page import get_all_credentials, get_login_params

# Получить все credentials
creds = get_all_credentials()
# {'standard_user': 'secret_sauce', 'problem_user': 'secret_sauce', ...}

# Получить параметры для параметризованного теста
username, password, should_login, expected_title, expected_error = get_login_params(0)
```

**JavaScript:**
```javascript
import { config, users, loginTests } from '../config.js';

// Основные credentials
const { username, password } = config;

// Все пользователи
const allUsers = users; // [{ username: 'standard_user', password: 'secret_sauce' }, ...]

// Параметры для параметризованного теста
const testParams = loginTests;
```
