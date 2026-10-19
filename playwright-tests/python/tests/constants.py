"""
Timeout and test data constants
"""

DEFAULT_TIMEOUT = 10000
NAVIGATION_TIMEOUT = 30000
ERROR_TIMEOUT = 3000
ASSERT_TIMEOUT = 5000

# performance_glitch_user is deliberately slow - needs room to log in
SLOW_TIMEOUT = 60000

# Expected number of products on the inventory page
EXPECTED_PRODUCT_COUNT = 6

# Performance budgets (business thresholds, not wait limits)
PERFORMANCE_BUDGETS = {
    "LOGIN_PAGE": 3000,
    "INVENTORY_PAGE": 3000,
    "PRODUCTS_LIST": 5000,
    "CART_PAGE": 4000,
    "AVERAGE_LOAD": 5000,
    "IMAGES": 3000,
}

# ---------------------------------------------------------------------------
# Login test data (mirrors TS loginTests in config.ts)
# ---------------------------------------------------------------------------

# Rows of the parameterised login test
LOGIN_TEST_PARAMS = [
    ("standard_user", "secret_sauce", True, "Products", None),
    ("problem_user", "secret_sauce", True, "Products", None),
    ("performance_glitch_user", "secret_sauce", True, "Products", None),
    ("error_user", "secret_sauce", True, "Products", None),
    ("visual_user", "secret_sauce", True, "Products", None),
    ("locked_out_user", "secret_sauce", False, None, "locked out"),
    ("standard_user", "wrong_password", False, None, "do not match"),
    ("", "secret_sauce", False, None, "Username is required"),
]

# All users and their passwords from SAUCE_DEMO_PASSWORDS env var
def get_all_credentials():
    """Return a dict of all users and their passwords from SAUCE_DEMO_PASSWORDS"""
    import os
    passwords_str = os.getenv("SAUCE_DEMO_PASSWORDS", "")
    if not passwords_str:
        return {}
    credentials = {}
    for pair in passwords_str.split(","):
        username, password = pair.split(":")
        credentials[username] = password
    return credentials


def get_login_params(index=None):
    """
    Return the parameters for the parameterised test.
    If index is given, return that case; otherwise the standard credentials.
    """
    if index is not None and index < len(LOGIN_TEST_PARAMS):
        return LOGIN_TEST_PARAMS[index]
    return ("standard_user", "secret_sauce", True, "Products", None)
