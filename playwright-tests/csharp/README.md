# SauceDemo e2e tests — C# / Playwright for .NET / xUnit

A fourth implementation of the shared SauceDemo scenario suite: **.NET 10 + Playwright for
.NET (1.63.0, chromium) + xUnit v2.9.3**. It mirrors the reference suites in
`playwright-tests/typescript` (the reference implementation), `playwright-tests/js` and
`playwright-tests/python` — the same 29 scenarios, the same page objects, the same
behaviour. Nothing in those three suites is used or modified by this one.

Result of the last run: `58 tests — 57 passed, 1 skipped (24-about-page, mirrored skip),
0 failed`.

---

## Stack

| Part | Version |
|---|---|
| .NET SDK / TFM | 10.0 (net10.0) |
| Test framework | xUnit 2.9.3 + xunit.runner.visualstudio 3.1.4 |
| Browser driver | Microsoft.Playwright 1.63.0 |
| Browser | Chromium (Chrome for Testing, installed by the Playwright CLI) |
| Target site | https://www.saucedemo.com |

## Requirements

- .NET 10 SDK
- A chromium build installed once for the project:

```powershell
dotnet tool install --global Microsoft.Playwright.CLI
playwright install chromium
```

`playwright install chromium` downloads Chrome for Testing into the per-user ms-playwright
cache, so it is a one-time step per machine/user. If the tool's app host cannot find a .NET
runtime (common with a SDK-only install), export `DOTNET_ROOT` to the SDK directory first.

## Running

All commands run from the project directory `playwright-tests/csharp/PlaywrightTests`
(a second `SauceDemoTests.csproj` lives one level up in the same folder and is a different
suite, so `dotnet test` from `playwright-tests/csharp` would pick up both):

```powershell
cd playwright-tests\csharp\PlaywrightTests

# everything
dotnet test

# one scenario class
dotnet test --filter "FullyQualifiedName~SortByPriceTests"

# one test
dotnet test --filter "FullyQualifiedName~SortByNameTests.Sort_by_name_a_to_z"

# slower, more verbose console output
dotnet test --logger "console;verbosity=detailed"
```

The build is part of `dotnet test`; `dotnet build` and `dotnet test --list-tests` also work on
their own.

### Configuration (environment variables)

| Variable | Default | Meaning |
|---|---|---|
| `SAUCE_DEMO_USERNAME` | `standard_user` | account used by the login helper |
| `SAUCE_DEMO_PASSWORD` | `secret_sauce` | its password |
| `SAUCE_DEMO_BASE_URL` | `https://www.saucedemo.com` | base URL of the shop |
| `SAUCE_DEMO_IGNORE_HTTPS_ERRORS` | `true` | accept the TLS-intercepting proxy certificate (see *Deviations*) |

The per-user passwords of the reference configs are supported as well
(`SAUCE_DEMO_PASSWORD_STANDARD`, `..._PROBLEM`, `..._PERFORMANCE`, `..._ERROR`,
`..._VISUAL`, `..._LOCKED`); the public demo value is the fallback. Nothing is hardcoded in
the page objects — only names of accounts (`locked_out_user`, …) and catalogue data.

## Layout

```
csharp/
└── PlaywrightTests/
    ├── PlaywrightTests.csproj        # xUnit + Microsoft.Playwright
    ├── xunit.runner.json             # 2 parallel threads, collections in parallel
    ├── Config/
    │   ├── TestConfig.cs             # env config, accounts, login test rows, expected product count
    │   ├── Timeouts.cs               # DEFAULT / NAVIGATION / SLOW / ERROR / ASSERT / ACTION
    │   └── PerformanceBudgets.cs     # named budgets in ms
    ├── Fixtures/PlaywrightFixture.cs # shared chromium + one context+page per test
    ├── Infrastructure/E2eTestBase.cs # per-test page, login helpers, money assertion
    ├── PageObjects/                  # LoginPage, InventoryPage, CartPage, CheckoutPage
    ├── Support/Money.cs              # "$1,234.56" parsing (cart + checkout)
    └── Tests/E2e/                    # 29 classes, one per reference scenario file
```

### Test isolation

xUnit builds the test class **per test method**, so `E2eTestBase.InitializeAsync()` opens a
**fresh browser context and page for every test**, from one shared chromium instance. That
matches the reference suites (where the `page` fixture is per test) and keeps tests
independent: each test logs in on its own, and SauceDemo's cart/session — which live in
browser storage — cannot leak into another test. Test classes run in parallel, two at a
time (`xunit.runner.json`).

## Scenario mapping

Reference files are the `NN-*.spec.js` files of `playwright-tests/js/tests/e2e/`
(equivalently `.spec.ts` in `typescript/`, `test-NN-*.py` in `python/`). Each scenario is one
C# test class; each class carries the reference number as the `Scenario` trait.

| Reference file | Class | Tests |
|---|---|---|
| `01-login` | `LoginTests` | Successful login with the standard user |
| `02-invalid-password` | `InvalidPasswordTests` | Invalid password shows an authentication error |
| `03-locked-user` | `LockedUserTests` | Locked out user sees the locked out error |
| `04-parameterized-login` | `ParameterizedLoginTests` | Login with an account shows the expected outcome (8 accounts, `[Theory]`) |
| `05-logout` | `LogoutTests` | Logout signs out of the account |
| `06-logout-from-pages` | `LogoutFromPagesTests` | Logout from the inventory page; Logout from the cart page |
| `07-sort-by-price` | `SortByPriceTests` | Sort by price low to high; Sort by price high to low |
| `08-sort-by-name` | `SortByNameTests` | Sort by name a to z |
| `09-item-detail` | `ItemDetailTests` | Item detail can be viewed and returned from |
| `10-multiple-users-catalog` | `MultipleUsersCatalogTests` | All users see the same catalogue |
| `11-product-images` | `ProductImagesTests` | All product images are displayed |
| `12-product-cards` | `ProductCardsTests` | Every product card has a name, a description, a price and an add button |
| `13-add-to-cart` | `AddToCartTests` | Adding a product to the cart |
| `14-remove-from-cart` | `RemoveFromCartTests` | Removing a product from the cart |
| `15-cart-quantity` | `CartQuantityTests` | Cart badge increments on add and decrements on remove |
| `16-add-remove-all` | `AddRemoveAllTests` | Adding and removing all products |
| `17-quantity-race-condition` | `QuantityRaceConditionTests` | Every cart item shows quantity one and offers no way to edit it; Every product lands in the cart when add buttons are clicked concurrently |
| `18-item-combinations` | `ItemCombinationsTests` | Cheapest three; Most expensive three; Alternating products |
| `19-add-all-six-products` | `AddAllSixProductsTests` | All six can be added; Product count and prices are valid; All six appear in the cart with their names |
| `20-full-purchase-flow` | `FullPurchaseFlowTests` | A full purchase from login to order confirmation |
| `21-checkout-validation` | `CheckoutValidationTests` | Empty checkout fields report each missing field |
| `22-export-pdf` | `ExportPdfTests` | An order can be exported as a pdf |
| `23-hamburger-menu` | `HamburgerMenuTests` | Hamburger menu can be opened and closed |
| `24-about-page` | `AboutPageTests` | *skipped* — see below |
| `25-responsive-design` | `ResponsiveDesignTests` | Mobile viewport; Tablet viewport; Viewport resize during interaction; Add to cart on mobile; Checkout on tablet |
| `26-browser-navigation` | `BrowserNavigationTests` | Back from cart; Forward after back; Back from checkout; Cart survives back/forward |
| `27-performance-budget` | `PerformanceBudgetTests` | Login page < 3s; Inventory < 3s; All products < 5s; Cart < 4s; Average of 3 runs < 5s; Images < 3s |
| `28-reset-app-state` | `ResetAppStateTests` | Reset clears the badge; Reset clears the stored cart but not the catalogue |
| `29-social-media-links` | `SocialMediaLinksTests` | Expected targets; Opens in a new tab; Shop stays usable after a link was opened |

`18-item-combinations`, `17-quantity-race-condition`, `19-add-all-six-products`,
`25-responsive-design`, `26-browser-navigation`, `27-performance-budget`, `28-reset-app-state`
and `29-social-media-links` use a `Test.describe` in the reference suites; the C# mirror uses
one class per file instead, with the class as the grouping unit.

## Deviations from the reference suites

1. **TLS interception (`IgnoreHTTPSErrors`).** This network terminates HTTPS with its own
   CA ("Cloudflare TLS proxy-everything Intercept CA"). Chromium only reads the NSS database,
   not the system bundle, so every navigation failed with `ERR_CERT_AUTHORITY_INVALID`.
   Contexts are therefore created with `IgnoreHTTPSErrors = TestConfig.IgnoreHttpsErrors`.
   Set `SAUCE_DEMO_IGNORE_HTTPS_ERRORS=false` on a network without a TLS proxy to get strict
   verification. The reference suites do not set it, because the proxy they run behind is
   different (their tooling uses the system CA bundle).
2. **Isolation is per test, not per class.** A class fixture (one page per test class) leaked
   cart state between the tests of one class — the badge of one test broke the `Add to cart`
   click of the next one, because the button of an already-added product is `Remove`.
   xUnit's per-method class instance gives the same per-test page the reference fixtures give.
3. **Timeouts.** `Timeouts.Action` (60s) is the per-action budget, as `actionTimeout` in the
   JS/TS config. The reference's hardcoded `timeout: 10000` on
   `test(...)`/`waitForPageLoad()` is expressed through `Timeouts.Default` / `Timeouts.Navigation`
   instead of literals. `InventoryPage.WaitForPageLoadAsync` waits the `NAVIGATION` budget
   (30s) rather than the reference's 10s, because the demo stand throttles parallel
   connections and the header renders before the list does; the list itself is then waited
   for web-first with `ToHaveCountAsync`.
4. **Money is `decimal`, compared exactly.** Cart, item total, tax (8%) and total are parsed
   with cents and compared after rounding to two places (`AssertAmountEqual`); `tax` and
   `total` are checked as relationships, exactly like the reference.
5. **Parallelism is two threads** (`xunit.runner.json`) against the reference's three workers:
   two cores, and saucedemo.com throttles parallel connections.
6. **24-about-page stays skipped**, mirroring the reference: the About link leads to
   saucelabs.com, which answers 403. The skip reason is stated on the `[Fact(Skip = "...")]`.
7. **23-hamburger-menu counts 5 menu items**, verified against the live site (All Items,
   Dynamic Catalog, About, Logout, Reset App State); the reference suites assert the same 5.
8. Page objects contain only what the scenarios use — the unused members of the reference
   page objects (`addProductByName`, `getProductElement`, `isErrorMessageVisible`, …) are not
   carried over, so there is no dead code to maintain.

## Rules the suite follows

- Web-first assertions only (`Expect(...).ToHaveCountAsync/ToHaveTextAsync/ToHaveURLAsync/
  ToHaveCountAsync(0)` for a missing cart badge) — no `Assert.Equal(await locator.CountAsync())`
  without a retry.
- No `page.waitForTimeout`, no `try/catch` that swallows failures, no timing measurement
  without the named budgets of `PerformanceBudgets`.
- Every test asserts; counters and totals are exact numbers, not "something is there".
- Locators live in the page objects once; `[data-test="..."]` first, button roles instead of
  text selectors; the specs contain no locators at all.
