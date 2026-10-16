"""
Timeout constants
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
