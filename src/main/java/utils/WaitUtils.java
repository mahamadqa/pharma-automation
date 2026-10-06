package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.WaitForSelectorState;

import base.BaseTest;

public class WaitUtils {

	private static final Logger log = LogManager.getLogger(WaitUtils.class);
	private Page page;

	public WaitUtils(Page page) {
		this.page = page;
	}

	/**
	 * Waits for the application's loading indicator (ZK loader) to appear and then disappear.
	 *
	 * @param page Playwright Page instance
	 */
	public static void waitForLoadingIndicatorToDisappear(Page page) {
		if (page == null) {
			page = BaseTest.getPage();
		}
		if (page == null) {
			log.warn("Cannot wait for loading indicator: Page instance is null.");
			return;
		}

		Locator loader = page.locator("//div[contains(@class,'z-loading-indicator')]");

		try {
			// Give the application a chance to display the loader
			loader.waitFor(new Locator.WaitForOptions()
					.setState(WaitForSelectorState.VISIBLE)
					.setTimeout(1000));
			log.info("Loading indicator displayed, waiting for it to disappear...");
		} catch (PlaywrightException e) {
			// Loader did not appear - that's okay
		}

		try {
			// If it appeared, wait until it disappears
			loader.waitFor(new Locator.WaitForOptions()
					.setState(WaitForSelectorState.HIDDEN)
					.setTimeout(10000));
			log.info("Loading indicator disappeared.");
		} catch (PlaywrightException e) {
			throw new RuntimeException("Loading indicator did not disappear within 10 seconds", e);
		}
	}

	

	
}
