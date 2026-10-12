import { test, expect } from "../fixtures";

/**
 * Social links at the bottom of the screen (X, Facebook, LinkedIn)
 */
test.describe("Social Media Links", () => {
  test("should show all three social links with the expected targets", async ({
    authenticatedPage,
  }) => {
    const { inventoryPage } = authenticatedPage;

    await expect(inventoryPage.socialXLink).toHaveAttribute(
      "href",
      "https://x.com/saucelabs",
    );
    await expect(inventoryPage.socialFacebookLink).toHaveAttribute(
      "href",
      "https://www.facebook.com/saucelabs",
    );
    await expect(inventoryPage.socialLinkedinLink).toHaveAttribute(
      "href",
      "https://www.linkedin.com/company/sauce-labs/",
    );
  });

  test("should open each social link in a new tab", async ({
    authenticatedPage,
  }) => {
    const { page, inventoryPage } = authenticatedPage;

    for (const link of [
      inventoryPage.socialXLink,
      inventoryPage.socialFacebookLink,
      inventoryPage.socialLinkedinLink,
    ]) {
      await expect(link).toHaveAttribute("target", "_blank");
      await expect(link).toHaveAttribute("rel", /noreferrer/);
    }

    // Follow the X link and confirm it leaves the shop
    const [popup] = await Promise.all([
      page.waitForEvent("popup"),
      inventoryPage.socialXLink.click(),
    ]);
    await expect(popup).toHaveURL(/x\.com/);
  });

  test("should keep the inventory page usable after opening a social link", async ({
    authenticatedPage,
  }) => {
    const { page, inventoryPage } = authenticatedPage;

    const [popup] = await Promise.all([
      page.waitForEvent("popup"),
      inventoryPage.socialFacebookLink.click(),
    ]);
    await popup.close();

    // The shop is untouched and still holds the products
    await expect(page).toHaveURL(/.*inventory\.html/);
    await expect(inventoryPage.products).toHaveCount(6);
  });
});
