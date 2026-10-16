package config;

import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;

/**
 * Playwright JUnit configuration.
 * Sets baseURL and other options for tests.
 */
public class PlaywrightConfig implements OptionsFactory {
    @Override
    public Options getOptions() {
        return new Options()
                .setBaseUrl(TestConfig.BASE_URL)
                .setHeadless(true);
    }
}
